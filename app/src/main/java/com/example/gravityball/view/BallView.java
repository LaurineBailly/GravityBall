package com.example.gravityball.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.gravityball.R;

public class BallView extends View {

    // Pencil that will allow the picture to be drawn
    private Paint picturePainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Bitmap that will be drawn
    private Bitmap ballPicture;

    // Picture position from left and top of the view component
    private double posTop = 0;
    private double posLeft = 0;

    // Variables stocking view and ballview graphic properties
    private int viewTop = 0;
    private int viewBottom = 0;
    private int viewLeft = 0;
    private int viewRight = 0;
    private int ballHeight = 0;
    private int ballWidth = 0;

    // Velocity of the ball in pixels per second
    private double xVelocity = 0;
    private double yVelocity = 0;

    // Factor to multiply the speed of the ball by
    private float factorSpeed = 1;

    // Period after which the ball position is updated in seconds
    private double periodUpdatePosSec = 0;

    // Number of pixels in 1 mm
    private static double pixelsInOneMm = 160/25.4;

    // Component view constructor with the physical layout
    // Getting the screenMetrics of the activity that instantiated a BallView object
    public BallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // DisplayMetrics structure describing general information about a display, such as its size,
        // density, and font scaling.
        DisplayMetrics screenMetrics = context.getResources().getDisplayMetrics();

        // getResources().getDisplayMetrics().density is the scaling factor for the Density
        // Independent Pixel unit, where one DIP is one pixel on an approximately 160 dpi screen
        // density of the screen (pixels per inch). DENSITY_DEFAULT = 160.
        // getting the number of pixels in 1 inch
        double density = 160*screenMetrics.density;

        if(density != 0) {
            // 25.4mm = 1 inch.
            // Getting the number of pixels in 1mm.
            pixelsInOneMm = density/25.4;
        }
    }

    // Set new position values of the ball
    public void setPosition(double aX, double aY) throws Exception {

        // periodUpdatePosSec not set
        if(periodUpdatePosSec == 0) {
            throw new Exception("The update period of the view has not been set.");
        }

        // Conversion of the acceleration data (meter/s2 --> pixels/s2)
        // Factor speed set to the acceleration
        // The X position axis and X acceleration axis on the device are opposite. We put the
        // acceleration axis in the same direction of the position and therefore velocity ones.
        double xAcceleration = -aX*1000*pixelsInOneMm*factorSpeed;
        double yAcceleration = aY*1000*pixelsInOneMm*factorSpeed;

        // Acceleration components corresponding to the position delta due to acceleration only

        // (1/2)*Ax(t)*t^2
        // where t is time and Ax is the accelerations on X axis (pixels/s2)
        double xMoveAx = 0.5*xAcceleration*periodUpdatePosSec*periodUpdatePosSec;

        // (1/2)*Ay(t)*t^2
        // where t is time and Ay is the accelerations on Y axis (pixels/s2)
        double yMoveAy = 0.5*yAcceleration*periodUpdatePosSec*periodUpdatePosSec;

        // Factor speed set to the velocity
        xVelocity = xVelocity*factorSpeed;
        yVelocity = yVelocity*factorSpeed;

        // Sx = Sx(t-1) + xMoveAx + Ux(t-1)*t
        // where Sx is position, Ux is initial velocity, t is time.
        posLeft = posLeft + xMoveAx + xVelocity*periodUpdatePosSec;

        // Sy = Sy(t-1) + yMoveAy + Uy(t-1)*t
        // where Sy is position, Uy is initial velocity, t is time.
        posTop = posTop + yMoveAy + yVelocity*periodUpdatePosSec;

        // Determining the Y position value

        // If the ball reaches the Top of the screen, the ball does not get out of the screen and
        // has no speed on this axis
        if(posTop < viewTop) {
            posTop = viewTop;
            yVelocity = -yVelocity;
        }

        // If the ball reaches the bottom of the screen, the ball does not get out of the screen and
        // has no speed on this axis
        else if(posTop > (viewBottom - ballHeight)) {
            posTop = viewBottom - ballHeight;
            yVelocity = -yVelocity;
        }
        else {

            // Vy(t) = Ay*t + Vy0
            // where t is time, Ay is the accelerations on Y axis (pixels/s2) and Vy0 is the initial
            // speed of the ball
            yVelocity = yAcceleration*periodUpdatePosSec + yVelocity;
        }

        // Determining the X position value

        // If the ball reaches the left of the screen, the ball does not get out of the screen and
        // has no speed on this axis
        if(posLeft <= viewLeft) {
            posLeft = viewLeft;
            xVelocity = -xVelocity;
        }

        // If the ball reaches the right of the screen, the ball does not get out of the screen and
        // has no speed on this axis
        else if(posLeft >= (viewRight - ballWidth)) {
            posLeft = viewRight - ballWidth;
            xVelocity = -xVelocity;
        }
        else {

            // Vx(t) = Ax*t + Vx0
            // where t is time, Ax is the accelerations on X axis (pixels/s2) and Vx0 is the initial
            // speed of the ball
            xVelocity = xAcceleration*periodUpdatePosSec + xVelocity;
        }
    }

    // onSizeChanged is called each time the size view changes, here only once because the activity
    // in which the view is displayed has been stacked in portrait mode (see manifest file).
    // onSizeChanged is called before onDraw.
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        // Loading picture into bitmap
        ballPicture = BitmapFactory.decodeResource(getResources(), R.drawable.bille);

        // Updating the values of ballview graphic properties
        ballHeight = ballPicture.getHeight();
        ballWidth = ballPicture.getWidth();
        viewTop = getTop();
        viewBottom = getBottom();
        viewLeft = getLeft();
        viewRight = getRight();

        // Getting left and top position for a picture at the center of the view component
        posLeft = (w - ballWidth)/2f;
        posTop = (h - ballHeight)/2f;
    }

    // onDraw is called by the system each time the view component is displayed or updated
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Drawing the picture in the middle of the view component
        canvas.drawBitmap(ballPicture, (int)posLeft, (int)posTop, picturePainter);
    }

    // performClick method for a better accessibility
    @Override
    public boolean performClick() {
        super.performClick();

        // The old view redraws with the new view (onDraw method is called)
        invalidate();
        return true;
    }

    public double getPosTopMm() {
        return posTop/pixelsInOneMm;
    }

    public double getPosLeftMm() {
        return posLeft/pixelsInOneMm;
    }

    public void setPeriodUpdatePosSec(double periodUpdatePosSec) throws Exception {
        this.periodUpdatePosSec = periodUpdatePosSec;
        if(periodUpdatePosSec <= 0) {
            throw new Exception("The period to update the view can not be nul or negative");
        }
    }

    public void setFactorSpeed(float factorSpeed) {
        if(factorSpeed != 0) {
            this.factorSpeed = factorSpeed;
        }
    }
}