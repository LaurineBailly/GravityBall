package com.example.gravityball.modele;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.gravityball.view.BallView;

// The phone class instanciates the accelerometer and manages its data
public class Phone implements SensorEventListener {

    // Current acceleration in X and Y
    private float ax;
    private float ay;

    // SensorManager lets you access the device's sensors.
    // Sensor is a class representing a sensor.
    private SensorManager sensorManager;
    private Sensor accelerometer;

    // Conversion 1mm to 1m
    private final double METERS_ONE_MM = 0.001;

    // Getting the size of one pixel in mm
    private double mmOnePixel;

    // Phone constructor that instanciates the accelerometer
    public Phone(Context context, double mmOnePixel) {
        sensorManager = (SensorManager)context.getSystemService(context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometer,sensorManager.SENSOR_DELAY_NORMAL);
        this.mmOnePixel = mmOnePixel;
    }

    // Acceleration ax in pixels per second
    public int getAx() {
        int axPixels = (int)(-ax/METERS_ONE_MM/mmOnePixel);
        return axPixels;
    }

    // Acceleration ay in pixels per second
    public int getAy() {
        int ayPixels = (int)(ay/METERS_ONE_MM/mmOnePixel);
        return ayPixels;
    }

    // When the acceleration values change onSensorChanged is called and they are loaded to ax and
    // ay (the check is done every 3us (sensorManager.SENSOR_DELAY_NORMAL))
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        ax = sensorEvent.values[0];
        ay = sensorEvent.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // Function that ages the x and y values of acceleration
    public void cancelListenerAccelerometer() {
        sensorManager.unregisterListener(this);
    }
}
