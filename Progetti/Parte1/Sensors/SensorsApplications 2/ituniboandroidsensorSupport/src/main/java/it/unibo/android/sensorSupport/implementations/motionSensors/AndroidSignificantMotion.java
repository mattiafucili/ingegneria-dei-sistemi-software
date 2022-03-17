package it.unibo.android.sensorSupport.implementations.motionSensors;

import it.unibo.android.sensorSupport.implementations.AndroidSensor;
import it.unibo.android.sensorSupport.interfaces.motionSensors.IAndroidDetectionSensor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import it.unibo.android.sensorData.implementation.AndroidDetectionSensorData;
import it.unibo.android.sensorData.interfaces.IAndroidDetectionSensorData;

public class AndroidSignificantMotion extends AndroidSensor implements
		IAndroidDetectionSensor{

	private static AndroidSignificantMotion[] instances = new AndroidSignificantMotion[4];
	protected IAndroidDetectionSensorData sensorData;
	
	/**
	 * This method returns, if exist, an instance of AndroidSignificantMotion, else
	 * the AndroidSignificantMotion is created and returned.
	 * 
	 * @param manager the instance of SensorManager
	 * @param delay the registration delay defined by one of <b>SensorManager.SENSOR_DELAY_*</b>
	 * 
	 * @return the instance of AndroidSignificantMotion
	 */
	public static AndroidSignificantMotion getInstance(SensorManager manager, int delay){
		Sensor significantMotion = manager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
		if(significantMotion != null){
			int index;
			switch (delay){
				case SensorManager.SENSOR_DELAY_NORMAL:
					index = 0;
					break;
				case SensorManager.SENSOR_DELAY_UI:
					index = 1;
					break;
				case SensorManager.SENSOR_DELAY_GAME:
					index = 2;
					break;
				case SensorManager.SENSOR_DELAY_FASTEST:
					index = 3;
					break;
				default: return null;
			}
			if (instances[index] == null)
				instances[index] = new AndroidSignificantMotion(manager, significantMotion, delay);
			return instances[index];
		}
		else
			return null;
	}
	
	private AndroidSignificantMotion(SensorManager manager, Sensor significantMotion, int delay){
		super(manager);
		manager.registerListener(this, significantMotion, delay);
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		sensorData = new AndroidDetectionSensorData(event.values, event.accuracy, event.timestamp);
		this.update(this, sensorData);
	}
	
	@Override
	public IAndroidDetectionSensorData getData(){
		return sensorData;
	}

	@Override
	public void unregister(){
		for(int i = 0; i < instances.length; i++)
			if(this.equals(instances[i]))
				instances[i] = null;
		super.unregister();
	}
}
