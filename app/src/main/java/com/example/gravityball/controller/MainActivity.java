package com.example.gravityball.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.gravityball.R;
import com.example.gravityball.modele.Accelerometer;
import com.example.gravityball.view.BallView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // A timer thanks to which the ball position will be updated
    Timer timerRefreshBallView;

    // Period after which the ball position is updated
    public static final int PERIOD_REFRESH_BALL_MS = 40;

    // Defining speed types
    public static final float SPEED_SLOW = 0.01f;
    public static final float SPEED_MEDIUM = 0.08f;
    public static final float SPEED_FAST = 0.64f;

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

            // Getting and setting to the ballView the ball speed
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
    }

    // The activity is visible on the screen
    @Override
    protected void onStart() {
        super.onStart();

        // Instantiating the timer
        timerRefreshBallView = new Timer();

        // timerRefreshBallView will tick every PERIOD_REFRESH_BALL_MS ms and starts immediately.
        timerRefreshBallView.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
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
            }
        }, 0, PERIOD_REFRESH_BALL_MS);
    }

    // The activity is running
    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.startListener();
    }

    // Something interrupted the activity
    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.cancelListener();
    }

    // The activity is not visible on the screen
    @Override
    protected void onStop() {
        super.onStop();
        timerRefreshBallView.cancel();
        timerRefreshBallView.purge();
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