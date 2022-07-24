package com.example.gravityball.controlller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.example.gravityball.R;
import com.example.gravityball.modele.Phone;
import com.example.gravityball.view.BallView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // A timer thanks to which the ball position will be updated
    Timer timer;

    // Timer period
    private final int TIMER_PERIOD = 20;

    // Conversion 1ms to s
    private final double ONE_MS_IN_S = 0.001;

    // ballView is the view including the ball and the area it can move in
    private BallView ballView;

    // Textviews that display the coordinates in mm
    private TextView tvXValue;
    private TextView tvYValue;

    // Conversion 1 inch to mm
    private final double ONE_INCH_IN_MM = 25.4;

    // True if the ball bitmap is loaded
    private boolean ballPictureLoaded;

    // Phone to manage the accelerometer
    Phone phone;

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

        // Getting the size of one pixel in mm
        final double mmOnePixel = getSizeOnePixel();

        // Listening when the onSizeChanged method is called on ballView, it allows to be sure that
        // the bipmap file is loaded.
        ballView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

                // The ball picture is loaded
                ballPictureLoaded = true;

                // Setting the coordinates values to the textviews in mm
                double xCoordinate = (ballView.getPosLeftDpx()*mmOnePixel);
                double yCoordinate = (ballView.getPosTopDpx()*mmOnePixel);
                tvXValue.setText(String.format("%.1f",xCoordinate));
                tvYValue.setText(String.format("%.1f",yCoordinate));
            }
        });

        // Creating the phone
        phone = new Phone(this,mmOnePixel);

        // Timer that will tick every TIMER_PERIOD ms
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(ballPictureLoaded) {

                    // Getting acceleration in pixels
                    double accelerationAx = phone.getAx();
                    double accelerationAy = phone.getAy();

                    // Setting the new position of the ball according to the accelaration noticed
                    // during the timer tick. We will consider it has been the same one during
                    // TIMER_PERIOD ms.
                    ballView.setPosition(accelerationAx,accelerationAy,TIMER_PERIOD*ONE_MS_IN_S,mmOnePixel);
                    ballView.performClick();

                    // Setting the coordinates values to the textviews in mm
                    double xCoordinate = (ballView.getPosLeftDpx()*mmOnePixel);
                    double yCoordinate = (ballView.getPosTopDpx()*mmOnePixel);
                    tvXValue.setText(String.format("%.1f",xCoordinate));
                    tvYValue.setText(String.format("%.1f",yCoordinate));
                }
            }
        },TIMER_PERIOD,TIMER_PERIOD);

    }

    // onResume is called when the activity is ready
    @Override
    protected void onResume() {
        super.onResume();

    }

    // onDestroy is called when the activity is destroyed (phone is sleeping or app killed)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        phone.cancelListenerAccelerometer();
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