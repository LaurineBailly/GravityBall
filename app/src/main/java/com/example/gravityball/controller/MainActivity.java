package com.example.gravityball.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gravityball.R;
import com.example.gravityball.modele.Accelerometer;
import com.example.gravityball.view.BallView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // A timer thanks to which the ball position will be updated
    Timer timerRefreshBallView;

    // Period after which the ball position is updated
    public static final int PERIOD_REFRESH_BALL_MS = 40;

    // True if the ball bitmap is loaded
    private boolean ballPictureLoaded;

    // ballView is the view including the ball and the area it can move in
    private BallView ballView;

    // An accelometer that provides data in pixels/s2
    Accelerometer accelerometer;

    // How many times the pixel accelerometer values should be checked per
    // PERIOD_REFRESH_BALL_POS_MS ms
    public static final int FREQ_CHECK_PIX_ACCELEROMETER = 1;

    // Textviews that display the coordinates in mm
    private TextView tvXValue;
    private TextView tvYValue;

    // onCreate is called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // The ball picture is not loaded yet
        ballPictureLoaded = false;

        // Getting the graphic components
        ballView = findViewById(R.id.id_ballView);
        tvXValue = findViewById(R.id.id_tvXValue);
        tvYValue = findViewById(R.id.id_tvYValue);

        // Indicating to the ballView the period with which it is updated in seconds
        try {
            ballView.setPeriodUpdatePosSec(PERIOD_REFRESH_BALL_MS*0.001);
        }

        // The period given is wrong
        catch (Exception e) {
            e.printStackTrace();
        }

        // Listening when the onSizeChanged method is called on ballView, it allows to be sure that
        // the bipmap file is loaded.
        ballView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

                // The ball picture is loaded
                ballPictureLoaded = true;

                // Display the coordinates of the ballView
                displayBallCoordinates();
            }
        });

        // sensorsOnDevice is the manager of the sensors on the device.
        SensorManager sensorsOnDevice = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Try to instantiate the accelerometer
        try {

            // Giving the check accelerometer values period to the Accelerometer in us: once between
            // the ballView updates and the list of devices.
            accelerometer = new Accelerometer(sensorsOnDevice, PERIOD_REFRESH_BALL_MS *1000/FREQ_CHECK_PIX_ACCELEROMETER);
        }

        // In case of a failure of the accelerometer initialization.
        catch (Exception e) {

            // The period given to the function is wrong
            if(e instanceof IllegalArgumentException){
                e.printStackTrace();
            }

            // Exception due to the device
            else {

                // A window pops up displaying the error and invites the user to close the application.
                // When the user presses close, onDestroy() is triggered.
                final EditText description = new EditText(this);
                AlertDialog.Builder errorPopUp = new AlertDialog.Builder(this);
                errorPopUp.setTitle("Error encountered");
                errorPopUp.setMessage("The following error has been encountered: " + e.toString());
                errorPopUp.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                errorPopUp.show();
            }
        }
    }

    // The activity is starting
    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.startListener();

        // Instantiating the timer
        timerRefreshBallView = new Timer();

        // timerRefreshBallView will tick every PERIOD_REFRESH_BALL_POS_MS ms and starts immediately.
        timerRefreshBallView.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(ballPictureLoaded) {

                    // Setting the new position of the ball according to the accelaration noticed
                    // during the timer tick.
                    try {
                        ballView.setPosition(accelerometer.getAx(), accelerometer.getAy());
                    }

                    // The period for updating the view has not been called
                    catch (Exception e) {
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

    // When the activity is no longer visible
    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.cancelListener();
        timerRefreshBallView.cancel();
    }

    // Setting the coordinates values of the ball to the textviews in mm
    public void displayBallCoordinates() {
        tvXValue.setText(String.format("%.1f", ballView.getPosLeftMm()));
        tvYValue.setText(String.format("%.1f", ballView.getPosTopMm()));
    }
}