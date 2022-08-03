package com.example.gravityball.modele;

import static android.content.Context.SENSOR_SERVICE;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

// The Accelerometer class instantiates an accelerometer and manages its data
public class Accelerometer implements SensorEventListener {

    // Current acceleration in X and Y in meters square
    private float aX = 0;
    private float aY = 0;

    // sensorsOnDevice represents the sensors on the device.
    // accelerometer represents the accelerometer on the device.
    private SensorManager sensorsOnDevice;
    private Sensor accelerometer;

    // Period after which the accelerometer values are checked in us.
    private int periodCheckValuesUs;

    public Accelerometer(SensorManager sensorsOnDevice, int periodCheckValuesUs) throws IllegalArgumentException, UnsupportedOperationException {
        this.sensorsOnDevice = sensorsOnDevice;

        // Set the accelerometer period in us
        if(periodCheckValuesUs <= 0) {
            throw new IllegalArgumentException("The period of the accelerometer can not be nul or negative");
        }
        else {
            this.periodCheckValuesUs = periodCheckValuesUs;
        }

        // Getting the accelerometer available by default on the phone, throws an exception if it
        // does not exist
        accelerometer = this.sensorsOnDevice.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer == null) {
            throw new UnsupportedOperationException("No accelerometer available on the device.");
        }

    }

    // Acceleration ax in meters per second square
    public double getAx() {
        return aX;
    }

    // Acceleration ay in meters per second square
    public double getAy() {
        return aY;
    }

    // Every periodCheckValuesUs us onSensorChanged is called and the acceleration values are loaded to
    // ax and ay
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        aX = sensorEvent.values[0];
        aY = sensorEvent.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // Stop checking the values
    public void cancelListener() {
        sensorsOnDevice.unregisterListener(this);
    }

    // Start checking the values
    public void startListener() {
        sensorsOnDevice.registerListener(this, accelerometer, periodCheckValuesUs);
    }
}
