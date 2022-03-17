package it.mattia.bluetoothleclient.services;

import java.util.UUID;

public final class Services {

    public static final UUID ACCELEROMETER_SERVICE = UUID.fromString("0000aa11-0000-1000-8000-00805f9b34fb");
    public static final UUID CHARACTERISTIC_ACCELEROMETER_VALUE = UUID.fromString("0000aa12-0000-1000-8000-00805f9b34fb");
    public static final UUID DESCRIPTOR_ACCELEROMETER_VALUE = UUID.fromString("0000aa13-0000-1000-8000-00805f9b34fb");
    public static final UUID GYROSCOPE_SERVICE = UUID.fromString("0000aa52-0000-1000-8000-00805f9b34fb");
    public static final UUID CHARACTERISTIC_GYROSCOPE_VALUE = UUID.fromString("0000aa51-0000-1000-8000-00805f9b34fb");
    public static final UUID DESCRIPTOR_GYROSCOPE_VALUE = UUID.fromString("0000aa53-0000-1000-8000-00805f9b34fb");
}
