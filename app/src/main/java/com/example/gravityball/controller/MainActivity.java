package com.example.gravityball.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.gravityball.R;
import com.example.gravityball.modele.Accelerometer;
import com.example.gravityball.view.BallView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Period after which we wish the ball position to be updated
    public static final int PERIOD_REFRESH_BALL_MS = 40;

    // Defining speed types
    public static final float SPEED_SLOW = 0.1f;
    public static final float SPEED_MEDIUM = 0.2f;
    public static final float SPEED_FAST = 0.4f;

    // True if the ball bitmap is loaded
    private boolean ballPictureLoaded = false;

    // ballView is the view including the ball and the area it can move in
    private BallView ballView;

    // An accelerometer that provides data in pixels/s2
    Accelerometer accelerometer;

    // How many times the accelerometer values should be checked per
    // PERIOD_REFRESH_BALL_MS ms
    public static final int FREQ_CHECK_ACCELEROMETER = 1;

    // Textviews that display the coordinates in mm
    private TextView tvXValue;
    private TextView tvYValue;

    // RadioButtons thanks to which the user sets the ball speed
    private RadioGroup rbgSpeed;

    // A handler is like a message queue that will process a runnable
    // Differences between a handler and a timer :
    // https://medium.com/@f2016826/timers-vs-handlers-aeae5d3cb5a
    private final Handler handler = new Handler();

    // The runnable assigned to the handler : updates the ball View
    private Runnable taskUpdateBallView;

    // onCreate is called when the activity is being open
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Forbidding phone from sleeping
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Radio buttons components
        RadioButton rbFast;
        RadioButton rbSlow;
        RadioButton rbMedium;

        // Getting the graphic components
        ballView = findViewById(R.id.id_ballView);
        tvXValue = findViewById(R.id.id_tvXValue);
        tvYValue = findViewById(R.id.id_tvYValue);
        rbFast = findViewById(R.id.id_rb_fast);
        rbSlow = findViewById(R.id.id_rb_slow);
        rbMedium = findViewById(R.id.id_rb_medium);
        rbgSpeed = findViewById(R.id.id_rbg_speed);

        // Register a callback to be invoked when one of the radio buttons is clicked.
        rbFast.setOnClickListener(this);
        rbSlow.setOnClickListener(this);
        rbMedium.setOnClickListener(this);

        // Indicating to the ballView the period with which it is updated in seconds
        try {
            ballView.setPeriodUpdatePosSec(PERIOD_REFRESH_BALL_MS*0.001);
        }

        // The period given is wrong
        catch(Exception e) {
            e.printStackTrace();
        }

        // Listening when the onSizeChanged method is called on ballView, it allows to be sure that
        // the bipmap file is loaded.
        ballView.addOnLayoutChangeListener((View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) -> {

            // The ball picture is loaded
            ballPictureLoaded = true;

            // Getting the ball speed and setting it to the ballView
            setBallSpeedSelectedByUser();

            // Display the coordinates of the ballView
            displayBallCoordinates();
        });

        // sensorsOnDevice is the manager of the sensors on the device.
        SensorManager sensorsOnDevice = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Try to instantiate the accelerometer
        try {

            // Giving the check values period to the Accelerometer in us
            // Once during PERIOD_REFRESH_BALL_MS
            accelerometer = new Accelerometer(sensorsOnDevice, PERIOD_REFRESH_BALL_MS*1000/FREQ_CHECK_ACCELEROMETER);
        }

        // In case of a failure of the accelerometer initialization.
        catch(Exception e) {

            // The period given to the function is wrong
            if(e instanceof IllegalArgumentException) {
                e.printStackTrace();
            }

            // Exception due to the device
            else {

                // A window pops up displaying the error and invites the user to close the application.
                // When the user presses close, onDestroy() is triggered.
                AlertDialog.Builder errorPopUp = new AlertDialog.Builder(this);
                errorPopUp.setTitle("Error encountered");
                errorPopUp.setMessage("The following error has been encountered: " + e);
                errorPopUp.setPositiveButton("Close", (DialogInterface dialog, int which) -> finish());
                errorPopUp.show();
            }
        }

        // Defining the task for updating the ballView
        taskUpdateBallView = new Runnable() {
            @Override
            public void run() {

                // Getting the current time in ms
                long currentTimeMillis = System.currentTimeMillis();

                // If the ball picture is loaded
                if(ballPictureLoaded) {

                    // Setting the new position of the ball according to the acceleration noticed
                    // during the timer tick.
                    try {
                        ballView.setPosition(accelerometer.getAx(), accelerometer.getAy());
                    }

                    // The period for updating the view has not been called
                    catch(Exception e) {
                        e.printStackTrace();
                    }

                    // Updating the view
                    ballView.performClick();

                    // Setting the coordinates values to the textviews in mm
                    displayBallCoordinates();
                }

                // Removing the callback for this task of the handler
                handler.removeCallbacks(taskUpdateBallView);

                // Time that took the previous code in this runnable
                long durationTaskUpdateBallView = currentTimeMillis - System.currentTimeMillis();

                // If we are already running out of time, this same task is started immediately
                if(durationTaskUpdateBallView <= PERIOD_REFRESH_BALL_MS) {
                    handler.post(taskUpdateBallView);
                }

                // Otherwise this same task is delayed
                else {
                    handler.postDelayed(taskUpdateBallView, PERIOD_REFRESH_BALL_MS - durationTaskUpdateBallView);
                }
            }
        };
    }

    // The activity is running
    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.startListener();

        // taskUpdateBallView starts
        handler.post(taskUpdateBallView);
    }

    // Something interrupted the activity
    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.cancelListener();

        // Removing the callback for taskUpdateBallView
        handler.removeCallbacks(taskUpdateBallView);
    }

    // Setting the coordinates values of the ball to the textviews in mm
    public void displayBallCoordinates() {
        tvXValue.setText(String.format(Locale.getDefault(),"%.1f", ballView.getPosLeftMm()));
        tvYValue.setText(String.format(Locale.getDefault(),"%.1f", ballView.getPosTopMm()));
    }

    // Called when a click is detected on one of the radio buttons
    @Override
    public void onClick(View v) {
        setBallSpeedSelectedByUser();
    }

    // Method that sets the speed factor to the ballView in function of the speed selected by the
    // user
    public void setBallSpeedSelectedByUser() {
        int idRbChecked = rbgSpeed.getCheckedRadioButtonId();
        if(idRbChecked == R.id.id_rb_slow) {
            ballView.setFactorSpeed(SPEED_SLOW);
        }
        else if(idRbChecked == R.id.id_rb_medium) {
            ballView.setFactorSpeed(SPEED_MEDIUM);
        }
        else if(idRbChecked == R.id.id_rb_fast) {
            ballView.setFactorSpeed(SPEED_FAST);
        }
        else {
            ballView.setFactorSpeed(SPEED_MEDIUM);
        }
    }
}