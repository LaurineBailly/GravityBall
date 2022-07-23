package com.example.gravityball.controlller;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.example.gravityball.R;
import com.example.gravityball.view.BallView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // SensorManager lets you access the device's sensors.
    // Sensor is a class representing a sensor.
    private SensorManager sensorManager;
    private Sensor sensor;

    // ballView is the view including the ball and the area it can move in
    private BallView ballView;

    // Textviews that display the coordinates in mm
    private TextView tvXValue;
    private TextView tvYValue;

    private final double ONE_INCH_IN_MM = 25.4;
    private final double MM_ONE_PIXEL = getSizeOnePixel();

    // onCreate is called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting the accelerometer sensor
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Getting the graphic components
        ballView = findViewById(R.id.id_ballView);
        tvXValue = findViewById(R.id.id_tvXValue);
        tvYValue = findViewById(R.id.id_tvYValue);

        // Listening when the onSizeChanged method is called on ballView, it allows to be sure that
        // the bipmap file is loaded.
        ballView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                double xCoordinate = (ballView.getPosLeftDpx()*MM_ONE_PIXEL);
                double yCoordinate = (ballView.getPosTopDpx()*MM_ONE_PIXEL);
                tvXValue.setText(String.format("%.1f",xCoordinate));
                tvYValue.setText(String.format("%.1f",yCoordinate));
            }
        });
    }

    // onResume is called when the activity is ready
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // The size of one pixel depends on the screen density
    // getResources().getDisplayMetrics().density is the scaling factor for the Density Independent
    // Pixel unit, where one DIP is one pixel on an approximately 160 dpi screen
    double getSizeOnePixel() {

        // A structure describing general information about a display, such as its size, density,
        // and font scaling.
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        // density of the screen (pixels per inch). DENSITY_DEFAULT = 160.
        double dip = displayMetrics.DENSITY_DEFAULT*displayMetrics.density;

        // size of one pixel
        double mmForOnePixel = ONE_INCH_IN_MM/dip;
        return mmForOnePixel;
    }
}