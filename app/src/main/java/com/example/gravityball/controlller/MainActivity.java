package com.example.gravityball.controlller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gravityball.R;
import com.example.gravityball.modele.PixelSizer;
import com.example.gravityball.modele.PixelsAccelerometer;
import com.example.gravityball.view.BallView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // A timer thanks to which the ball position will be updated
    Timer timerRefreshBallView;

    // Period after which the ball position is updated
    public static final int PERIOD_REFRESH_BALL_POS_MS = 40;

    // True if the ball bitmap is loaded
    private boolean ballPictureLoaded;

    // ballView is the view including the ball and the area it can move in
    private BallView ballView;

    // An accelometer that provides data in pixels/s2
    PixelsAccelerometer pixelsAccelerometer;

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

        // Indicating to the ballView the period with which it is updated
        ballView.setPeriodUpdatePosSec(PERIOD_REFRESH_BALL_POS_MS);

        // DisplayMatrics structure describing general information about a display, such as its size,
        // density, and font scaling. It is provided to PixelSize to allow conversions between metric
        // and pixels
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        PixelSizer.configure(displayMetrics);

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

        // Creating the pixelsAccelerometer which manages the acceleration measurements and gives it
        // in pixels/s2. sensorsOnDevice is the list of sensors on the device.
        SensorManager sensorsOnDvice = (SensorManager)getSystemService(SENSOR_SERVICE);
        pixelsAccelerometer = new PixelsAccelerometer(sensorsOnDvice);
        try {

            // Giving the check accelerometer values to the pixelAccelerometer : once between the
            // ballView updates.
            pixelsAccelerometer.initialize(PERIOD_REFRESH_BALL_POS_MS/FREQ_CHECK_PIX_ACCELEROMETER);
        }

        // In case of a failure of the pixelsAccelerometer initialization.
        catch (Exception e) {

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

    // The activity is starting
    @Override
    protected void onStart() {
        super.onStart();

        // Start checking the values of the accelerometer
        pixelsAccelerometer.startListener();

        // Instanciating the timer
        timerRefreshBallView = new Timer();

        // timerRefreshBallView will tick every PERIOD_REFRESH_BALL_POS_MS ms and starts immediately.
        timerRefreshBallView.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(ballPictureLoaded) {

                    // Setting the new position of the ball according to the accelaration noticed
                    // during the timer tick.
                    ballView.setPosition(pixelsAccelerometer);
                    ballView.performClick();

                    // Setting the coordinates values to the textviews in mm
                    displayBallCoordinates();
                }
            }
        }, 0, PERIOD_REFRESH_BALL_POS_MS);
    }

    // When the activity is no longer visible
    @Override
    protected void onStop() {
        super.onStop();
        if (pixelsAccelerometer != null) {
            pixelsAccelerometer.cancelListener();
        }
        if (timerRefreshBallView != null) {
            timerRefreshBallView.cancel();
        }
    }

    // Setting the coordinates values of the ball to the textviews in mm
    public void displayBallCoordinates() {
        tvXValue.setText(String.format("%.1f", PixelSizer.convertPixelsToMillimeters(ballView.getPosLeftDpx())));
        tvYValue.setText(String.format("%.1f", PixelSizer.convertPixelsToMillimeters(ballView.getPosTopDpx())));
    }
}