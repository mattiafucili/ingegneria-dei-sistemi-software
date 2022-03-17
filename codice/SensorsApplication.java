package com.erika.sensorsapplications;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends Activity {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private EditText accX, accY, accZ, gyrX, gyrY, gyrZ;
    private AccelerometerEventsListener accListener;
    private GyroscopeEventsListener gyrListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeResources();
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

        sensorManager.registerListener(accListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyrListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private class AccelerometerEventsListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            accX.setText("" + event.values[0]);
            accY.setText("" + event.values[1]);
            accZ.setText("" + event.values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    private class GyroscopeEventsListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            gyrX.setText("" + event.values[0]);
            gyrY.setText("" + event.values[1]);
            gyrZ.setText("" + event.values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}
