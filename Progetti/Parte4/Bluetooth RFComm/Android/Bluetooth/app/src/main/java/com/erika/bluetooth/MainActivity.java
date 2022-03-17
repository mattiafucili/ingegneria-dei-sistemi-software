package com.erika.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;

    final byte delimiter = 33;
    int readBufferPosition = 0;
    int i, j = 0;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private EditText accX, accY, accZ, gyrX, gyrY, gyrZ;
    private AccelerometerEventsListener accListener;
    private GyroscopeEventsListener gyrListener;
    //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
    private UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID

    public void sendBtMsg(String msg2send){

        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if (!mmSocket.isConnected())
                mmSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String msg = msg2send;
            //msg += "\n";
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());
            mmSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeResources();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("raspberrypi")) //Note, you will need to change this to match the name of your device
                {
                    mmDevice = device;
                    break;
                }
            }
        }


    }

    private void initializeResources() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accX = (EditText) findViewById(R.id.txtAccX);
        accY = (EditText) findViewById(R.id.txtAccY);
        accZ = (EditText) findViewById(R.id.txtAccZ);
        gyrX = (EditText) findViewById(R.id.txtGyrX);
        gyrY = (EditText) findViewById(R.id.txtGyrY);
        gyrZ = (EditText) findViewById(R.id.txtGyrZ);

        accListener = new AccelerometerEventsListener();
        gyrListener = new GyroscopeEventsListener();

        sensorManager.registerListener(gyrListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(accListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private class AccelerometerEventsListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            i = i + 1;
            if(i == 100) {
                i = 0;
                accX.setText("" + event.values[0]);
                accY.setText("" + event.values[1]);
                accZ.setText("" + event.values[2]);
                String msg = ("a-" + event.values[0] + "-" + event.values[1] + "-" + event.values[2]);
                sendBtMsg(msg);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    private class GyroscopeEventsListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            j = j + 1;
            if(j == 100){
                j = 0;
                gyrX.setText("" + event.values[0]);
                gyrY.setText("" + event.values[1]);
                gyrZ.setText("" + event.values[2]);
                String msg = ("g-" + event.values[0] + "-" + event.values[1] + "-" + event.values[2]);
                sendBtMsg(msg);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
