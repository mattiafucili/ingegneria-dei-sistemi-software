package it.mattia.bluetoothleclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import it.mattia.bluetoothleclient.services.Services;

public class GattClient {

    private static final String LOG = "****************";

    private Context context;
    private String deviceAddress;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;

    private String accelerometerValue;
    //private String gyroscopeValue;

    private TextView txtX, txtY, txtZ, txtDebug;

    // Getters
    public String getAccelerometerValue() {
        return accelerometerValue;
    }

    /*public String getGyroscopeValue() {
        return gyroscopeValue;
    }*/

    // CallBacks
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(LOG, "CONNECTED");
                txtDebug.append("Connected\n");
                gatt.discoverServices();
                Log.d(LOG, "STARTING DISCOVERY SERVICES");
                txtDebug.append("Starting discovery services\n");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(LOG, "DISCONNECTED");
                txtDebug.append("Disconnected\n");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                List<BluetoothGattService> services = gatt.getServices();

                for (BluetoothGattService service : services) {
                    if (service != null) {
                        if (service.getUuid().equals(Services.ACCELEROMETER_SERVICE)) {
                            BluetoothGattCharacteristic characteristic = service.getCharacteristic(Services.CHARACTERISTIC_ACCELEROMETER_VALUE);
                            if (characteristic != null) {
                                gatt.setCharacteristicNotification(characteristic, true);
                                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(Services.DESCRIPTOR_ACCELEROMETER_VALUE);
                                if (descriptor != null) {
                                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    gatt.writeDescriptor(descriptor);
                                    Log.d(LOG, "ENABLE ACCELEROMETER NOTIFICATION SENT");
                                    txtDebug.append("Enable accelerometer notification\n");
                                }
                            }
                        }/* else if (service.getUuid().equals(Services.GYROSCOPE_SERVICE)) {
                            BluetoothGattCharacteristic characteristic = service.getCharacteristic(Services.CHARACTERISTIC_GYROSCOPE_VALUE);
                            if (characteristic != null) {
                                gatt.setCharacteristicNotification(characteristic, true);
                                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(Services.DESCRIPTOR_GYROSCOPE_VALUE);
                                if (descriptor != null) {
                                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    gatt.writeDescriptor(descriptor);
                                    Log.d(LOG, "ENABLE GYROSCOPE NOTIFICATION SENT");
                                }
                            }
                        }*/
                    }
                }
            } else {
                txtDebug.append("Gatt failed\n");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (characteristic.getUuid().equals(Services.CHARACTERISTIC_ACCELEROMETER_VALUE) && status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(LOG, "READ ACCELEROMETER VALUE SUCCESS");
                txtDebug.append("Read accelerometer value success\n");
            } else if (characteristic.getUuid().equals(Services.CHARACTERISTIC_ACCELEROMETER_VALUE) && status == BluetoothGatt.GATT_FAILURE) {
                Log.d(LOG, "READ ACCELEROMETER VALUE FAILURE");
                txtDebug.append("Read accelerometer value failure\n");
            /*else if (characteristic.getUuid().equals(Services.CHARACTERISTIC_GYROSCOPE_VALUE) && status == BluetoothGatt.GATT_SUCCESS)
                Log.d(LOG, "READ GYROSCOPE VALUE SUCCESS");
            else if (characteristic.getUuid().equals(Services.CHARACTERISTIC_GYROSCOPE_VALUE) && status == BluetoothGatt.GATT_FAILURE)
                Log.d(LOG, "READ GYROSCOPE VALUE FAILURE");*/
            } else {
                Log.d(LOG, "READ UNKNOWN CHARACTERISTIC");
                txtDebug.append("Unknown characteristic\n");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (characteristic.getUuid().equals(Services.CHARACTERISTIC_ACCELEROMETER_VALUE)) {
                //accelerometerValue = characteristic.getValue().toString();
                char[] values = characteristic.toString().toCharArray();
                txtX.setText(values[0]);
                txtY.setText(values[1]);
                txtZ.setText(values[2]);
            }/*else if (characteristic.getUuid().equals(Services.CHARACTERISTIC_GYROSCOPE_VALUE))
                gyroscopeValue = characteristic.getValue().toString();*/
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (descriptor.getUuid().equals(Services.DESCRIPTOR_ACCELEROMETER_VALUE) && status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(LOG, "ENABLE ACCELEROMETER NOTIFICATION SUCCESS");
                txtDebug.append("Enable accelerometer notification success\n");
            } else if (descriptor.getUuid().equals(Services.DESCRIPTOR_ACCELEROMETER_VALUE) && status == BluetoothGatt.GATT_FAILURE) {
                Log.d(LOG, "ENABLE ACCELEROMETER NOTIFICATION FAILURE");
                txtDebug.append("Enable accelerometer notification failure\n");
            /*else if (descriptor.getUuid().equals(Services.DESCRIPTOR_GYROSCOPE_VALUE) && status == BluetoothGatt.GATT_SUCCESS)
                Log.d(LOG, "ENABLE GYROSCOPE NOTIFICATION SUCCESS");
            else if (descriptor.getUuid().equals(Services.DESCRIPTOR_GYROSCOPE_VALUE) && status == BluetoothGatt.GATT_FAILURE)
                Log.d(LOG, "ENABLE GYROSCOPE NOTIFICATION FAILURE");*/
            } else {
                Log.d(LOG, "ENABLE UNKNOWN DESCRIPTOR");
                txtDebug.append("Enable unknown descriptor\n");
            }
        }
    };

    // Broadcast Receivers
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    Log.d(LOG, "BLUETOOTH ENABLED");
                    startClient();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    stopClient();
                    break;
                default:
                    Log.d(LOG, "UNKNOWN BLUETOOTH STATE");
                    break;
            }
        }
    };

    // Methods
    public void onCreate(Context context, String deviceAddress, TextView txtDebug) throws RuntimeException {
        this.context = context;
        this.deviceAddress = deviceAddress;
        this.txtDebug = txtDebug;

        txtDebug.append("Device address: " + deviceAddress);

        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(bluetoothReceiver, filter);

        if (!bluetoothAdapter.isEnabled()) {
            Log.d(LOG, "BLUETOOTH IS CURRENTLY DISABLED");
            bluetoothAdapter.enable();
        } else {
            startClient();
        }
    }

    public void onDestroy() {
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter.isEnabled()) {
            stopClient();
        }

        context.unregisterReceiver(bluetoothReceiver);
        Log.d(LOG, "CLIENT DESTROYED");
    }

    public void startClient() {
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
        bluetoothGatt = bluetoothDevice.connectGatt(context, false, gattCallback);

        if (bluetoothGatt == null) {
            Log.d(LOG, "UNABLE TO CREATE GATT CONNECTION");
            txtDebug.append("Unable to create GATT connection\n");
            return;
        }

        Log.d(LOG, "CLIENT STARTS");
        txtDebug.append("Client starts\n");
    }

    private void stopClient() {
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }

        if (bluetoothAdapter != null) {
            bluetoothAdapter = null;
        }

        Log.d(LOG, "CLIENT STOPS");
        txtDebug.append("Client stops\n");
    }
}
