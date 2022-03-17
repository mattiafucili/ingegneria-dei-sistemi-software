package main;

import java.time.Duration;

import tinyb.BluetoothDevice;
import tinyb.BluetoothException;
import tinyb.BluetoothGattCharacteristic;
import tinyb.BluetoothGattDescriptor;
import tinyb.BluetoothGattService;
import tinyb.BluetoothManager;

public class Main {

	public static void main(String[] args) {
		boolean connect = false;
		
		BluetoothManager bluetoothManager = BluetoothManager.getBluetoothManager();
		bluetoothManager.startDiscovery();
		BluetoothDevice device = bluetoothManager.find(null, "0000aa11-0000-1000-8000-00805f9b34fb", null, Duration.ofSeconds(10));
		
		try {
            bluetoothManager.stopDiscovery();
        } catch (BluetoothException e) {
            System.err.println("Discovery could not be stopped right now");
        }

        if (device == null) {
            System.err.println("No sensor found with the provided address.");
            System.exit(-1);
        }

        if (device.connect())
            System.out.println("Sensor with the provided address connected");
        else {
            System.out.println("Could not connect device.");
            System.exit(-1);
        }
        
        connect = true;
        
        BluetoothGattService service = device.find("0000aa11-0000-1000-8000-00805f9b34fb");
        if (service == null) {
            System.err.println("This device does not have the accelerometer service we are looking for.");
            device.disconnect();
            System.exit(-1);
        }
        
        BluetoothGattCharacteristic characteristic = service.find("0000aa12-0000-1000-8000-00805f9b34fb");
        BluetoothGattDescriptor descriptor = characteristic.find("0000aa13-0000-1000-8000-00805f9b34fb");
        
        byte[] config = { 0x01 };
        descriptor.writeValue(config);
        
        while(connect) {
        	try {
	        	byte[] value = characteristic.readValue();
	        	System.out.println(new String(value).toString() + System.lineSeparator());
        	} catch(Exception e) {
        		connect = false;
        	}
        }
        
        device.disconnect();
	}
}
