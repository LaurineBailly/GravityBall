package com.example.gravityball.modele;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

// The phone class instanciates the accelerometer and manages its data
public class PixelsAccelerometer implements SensorEventListener {

    // Current acceleration in X and Y in meters square
    private float axMetersSec2;
    private float ayMetersSec2;

    // sensorsOnDevice represents the sensors on the device.
    // accelerometer is a class representing the accelerometer on the device.
    private SensorManager sensorsOnDevice;
    private Sensor accelerometer;

    // Period after which the accelerometer values are checked in us.
    private int periodCheckValUs;

    public PixelsAccelerometer(SensorManager sensorsOnDevice) {
        this.sensorsOnDevice = sensorsOnDevice;
    }

    // Subscribing to the accelerometer available on the device
    // Throws an exception if an accelerometer does not exist
    public void initialize(int periodCheckValMs) throws Exception {

        // Set the accelerometer period in us
        this.periodCheckValUs = periodCheckValMs*1000;

        // Getting the accelerometer available by default on the phone, throws an excepton if it
        // does not exist
        accelerometer = sensorsOnDevice.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer == null) {
            throw new Exception("No accelerometer available on the device.");
        }
    }

    // Acceleration ax in pixels per second square
    public int getAxPixelsPerSecondSquare() {
        return (int)(PixelSizer.convertMetersToPixels(axMetersSec2));
    }

    // Acceleration ay in pixels per second square
    public int getAyPixelsPerSecondSquare() {
        return (int)(PixelSizer.convertMetersToPixels(ayMetersSec2));
    }

    // Every periodCheckValUs us onSensorChanged is called and the acceleration values are loaded to
    // ax and ay
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        axMetersSec2 = sensorEvent.values[0];
        ayMetersSec2 = sensorEvent.values[1];
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
        sensorsOnDevice.registerListener(this, accelerometer, periodCheckValUs);
    }
}
