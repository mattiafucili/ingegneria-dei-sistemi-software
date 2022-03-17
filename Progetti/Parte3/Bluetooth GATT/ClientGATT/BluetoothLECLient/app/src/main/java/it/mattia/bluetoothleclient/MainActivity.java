package it.mattia.bluetoothleclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Set;
import java.util.StringTokenizer;

//https://github.com/NordicSemiconductor/Android-Scanner-Compat-Library/blob/master/scanner/src/main/java/no/nordicsemi/android/support/v18/scanner/ScanCallback.java
public class MainActivity extends AppCompatActivity {

    private BluetoothLeScanner scanner;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device = null;

    private Handler handler, handlerUI;

    private GattClient client = null;

    private final String LOG = "****************";

    private boolean scanning = false;

    private TextView txtDebug, txtX, txtY, txtZ;

    // Runnable
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };

    private final Runnable runnableUI = new Runnable() {
        @Override
        public void run() {
            String value = client.getAccelerometerValue();
            StringTokenizer tok = new StringTokenizer(value, ";");
            txtX.setText(tok.nextToken());
            txtY.setText(tok.nextToken());
            txtZ.setText(tok.nextToken());
        }
    };

    // Callbacks
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            txtDebug.append("Found: " + result.getDevice().getName());
            //startInteract(result.getDevice());
        }
    };

    // Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }

        handler = new Handler();
        handlerUI = new Handler();

        txtDebug = (TextView) findViewById(R.id.txtDebug);
        txtDebug.setMovementMethod(new ScrollingMovementMethod());

        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        if(!devices.isEmpty())
            for(BluetoothDevice pairedDevice : devices)
                if(pairedDevice.getAddress().equals("78:D7:5F:DB:45:6F")) {
                    device = pairedDevice;
                    Log.d(LOG, "DEVICE PAIRED");
                    txtDebug.append("Device paired\n");
                    break;
                }

        Button button = (Button) findViewById(R.id.btnStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetoothAdapter.isEnabled())
                    startScan();
            }
        });

        txtX = (TextView) findViewById(R.id.txtAccX);
        txtY = (TextView) findViewById(R.id.txtAccY);
        txtZ = (TextView) findViewById(R.id.txtAccZ);
    }

    private void startScan() {
        if(device == null) {
            handler.postDelayed(runnable, 10000);

            scanner.startScan(scanCallback);
            scanning = true;
            Log.d(LOG, "SCAN STARTS");
            txtDebug.append("Scan starts\n");
        } else {
            startInteract(device);
        }
    }

    private void stopScan() {
        if (scanning) {
            scanning = false;
            scanner.stopScan(scanCallback);
            Log.d(LOG, "SCAN STOPS");
            txtDebug.append("Scan stops\n");
            handler.removeCallbacks(runnable);
        }
    }

    private void startInteract(BluetoothDevice device) {
        client = new GattClient();
        client.onCreate(this, device.getAddress(), (TextView) findViewById(R.id.txtDebug));
        txtDebug.append("Create client\n");
        client.startClient();
        handlerUI.postDelayed(runnableUI, 1000);
    }

    /*public String getGyroscopeValue() {
        return client.getGyroscopeValue();
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.onDestroy();
    }
}
