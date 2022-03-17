package it.mattia.usbaccessory;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView txtDebug, txtX, txtY, txtZ;

    private static final String ACTION_USB_PERMISSION = "android.permission.USB_PERMISSION";

    private UsbManager usbManager;
    private UsbAccessory usbAccessory;

    private PendingIntent permissionIntent;

    private boolean start = false;

    private boolean permissionRequestPending;
    private ParcelFileDescriptor fileDescriptor;
    private FileOutputStream outputStream;

    // BroadCast receivers
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            txtDebug.append("USB attached\n");
            if (action.equals(ACTION_USB_PERMISSION)) {
                synchronized (this) {
                    UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openAccessory(accessory);
                        txtDebug.append("Permission granted\n");
                    } else {
                        txtDebug.append("Permission denied\n");
                    }
                    permissionRequestPending = false;
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                txtDebug.append("USB detached\n");
                if (accessory != null && accessory.equals(usbAccessory)) {
                    closeAccessory();
                }
            }
        }
    };

    // Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDebug = (TextView) findViewById(R.id.txtDebug);
        txtDebug.setMovementMethod(new ScrollingMovementMethod());
        txtX = (TextView) findViewById(R.id.txtAccX);
        txtY = (TextView) findViewById(R.id.txtAccY);
        txtZ = (TextView) findViewById(R.id.txtAccZ);

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(usbReceiver, filter);

        Button btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        AccelerometerEventsListener accListener = new AccelerometerEventsListener();

        sensorManager.registerListener(accListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        txtDebug.append("Init\n");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (outputStream != null) {
            return;
        }

        UsbAccessory[] accessories = usbManager.getAccessoryList();
        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            if (usbManager.hasPermission(accessory)) {
                openAccessory(accessory);
            } else {
                synchronized (usbReceiver) {
                    if (!permissionRequestPending) {
                        usbManager.requestPermission(accessory, permissionIntent);
                        permissionRequestPending = true;
                    }
                }
            }
        } else {
            txtDebug.append("Accessory null\n");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        closeAccessory();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(usbReceiver);
        super.onDestroy();
    }

    private void openAccessory(UsbAccessory accessory) {
        fileDescriptor = usbManager.openAccessory(accessory);
        if (fileDescriptor != null) {
            usbAccessory = accessory;
            FileDescriptor fd = fileDescriptor.getFileDescriptor();
            outputStream = new FileOutputStream(fd);
            start = true;
            txtDebug.append("Accessory open\n");
        } else {
            txtDebug.append("File descriptor null\n");
        }
    }

    private void closeAccessory() {
        try {
            if (fileDescriptor != null) {
                fileDescriptor.close();
            }
        } catch (IOException e) {
            txtDebug.append("Exception in close accessory: " + e.getMessage() + "\n");
        } finally {
            fileDescriptor = null;
            usbAccessory = null;
        }
        txtDebug.append("Close accessory\n");
    }

    // Listeners
    private class AccelerometerEventsListener implements SensorEventListener {

        int count = 1;

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(count == 20) {
                txtX.setText("" + event.values[0]);
                txtY.setText("" + event.values[1]);
                txtZ.setText("" + event.values[2]);

                if(start) {
                    try {
                        outputStream.write(new String(event.values[0] + ";" + event.values[1] + ";" + event.values[2]).getBytes());
                        txtDebug.append("Sent info\n");
                    } catch (IOException e) {
                        txtDebug.append("Exception in write: " + e.getMessage() + "\n");
                    }
                }
                count = 1;
            }
            count = count + 1;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}