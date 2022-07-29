package com.example.gravityball.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;

import com.example.gravityball.R;
import com.example.gravityball.modele.PixelsAccelerometer;

public class BallView extends View {

    // Pencil that will allow the picture to be drawn
    private Paint picturePainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Bitmap that will be drawn
    private Bitmap ballPicture;

    // Picture postion from left and top of the view component
    private double posTopDpx;
    private double posLeftDpx;

    // Variables stocking view and ballview graphic properties
    private int viewTop;
    private int viewBottom;
    private int viewLeft;
    private int viewRight;
    private int ballHeight;
    private int ballWidth;

    // Velocity of the ball in pixels per second
    private double xSpeedPixSec;
    private double ySpeedPixSec;

    // Period after which the ball position is updated in seconds
    private double periodUpdatePosSec;

    // Component view constructor with the physical layout
    public BallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // Component view constructor without the physical layout
    public BallView(Context context) {
        super(context);
    }

    // Set new position values of the ball
    public void setPosition(PixelsAccelerometer pixelsAccelerometer) {

        // Getting the acceleration data in pixels/s2
        // The X position axis and X acceleration axis on the device are opposite. We put the
        // acceleration axis in the same direction of the position and therefore velocity ones.
        int AxPixSec2 = -pixelsAccelerometer.getAxPixelsPerSecondSquare();
        int AyPixSec2 = pixelsAccelerometer.getAyPixelsPerSecondSquare();

        // Acceleration components corresponding to the position delta due to acceleration only

        // (1/2)*Ax(t)*t^2
        // where t is time and Ax is the accelerations on X axis (pixels/s2)
        double deltaAccLeftDpx = 0.5*AxPixSec2*periodUpdatePosSec*periodUpdatePosSec;

        // (1/2)*Ay(t)*t^2
        // where t is time and Ay is the accelerations on Y axis (pixels/s2)
        double deltaAccTopDpx = 0.5*AyPixSec2*periodUpdatePosSec*periodUpdatePosSec;

        // Velocity the ball should have with this acceleration, screen boundaries free, in pix/s

        // Vy(t) = Ay*t + Vy0
        // where t is time, Ay is the accelerations on Y axis (pixels/s2) and Vy0 is the initial
        // speed of the ball
        double yPureSpeedPixSecond = deltaAccTopDpx*periodUpdatePosSec + xSpeedPixSec;

        // Vx(t) = Ax*t + Vx0
        // where t is time, Ax is the accelerations on X axis (pixels/s2) and Vx0 is the initial
        // speed of the ball
        double xPureSpeedPixSecond = deltaAccLeftDpx*periodUpdatePosSec + xSpeedPixSec;

        // Position the ball should have with this acceleration, screen boundaries free

        // Sy = Sy(t-1) + deltaAccTopDpx + Uy(t-1)*t
        // where Sy is position, Uy is initial velocity, t is time.
        double purePosTopDpx = posTopDpx + deltaAccTopDpx + ySpeedPixSec*periodUpdatePosSec;

        // Sx = Sx(t-1) + deltaAccLeftDpx + Ux(t-1)*t
        // where Sx is position, Ux is initial velocity, t is time.
        double purePosLeftDpx = posLeftDpx + deltaAccLeftDpx + xSpeedPixSec*periodUpdatePosSec;

        // Determining the Y position value

        // If the ball reaches the Top of the screen, the ball does not get out of the screen and
        // has no speed on this axis
        if(purePosTopDpx <= viewTop) {
            posTopDpx = viewTop;
            ySpeedPixSec = 0;
        }

        // If the ball reaches the bottom of the screen, the ball does not get out of the screen and
        // has no speed on this axis
        else if(purePosTopDpx >= (viewBottom - ballHeight)) {
            posTopDpx = viewBottom - ballHeight;
            ySpeedPixSec = 0;
        }
        else {
            posTopDpx = purePosTopDpx;
            ySpeedPixSec = yPureSpeedPixSecond;
        }

        // Determining the X position value

        // If the ball reachs the left of the screen, the ball does not get out of the screen and
        // has no speed on this axis
        if(purePosLeftDpx <= viewLeft) {
            posLeftDpx = viewLeft;
            xSpeedPixSec = 0;
        }

        // If the ball reachs the right of the screen, the ball does not get out of the screen and
        // has no speed on this axis
        else if(purePosLeftDpx >= (viewRight - ballWidth)) {
            posLeftDpx = viewRight - ballWidth;
            xSpeedPixSec = 0;
        }
        else {
            posLeftDpx = purePosLeftDpx;
            xSpeedPixSec = xPureSpeedPixSecond;
        }
    }

    public double getPosTopDpx() {
        return posTopDpx;
    }

    public double getPosLeftDpx() {
        return posLeftDpx;
    }

    public void setPeriodUpdatePosSec(int periodUpdatePosMs) {
        periodUpdatePosSec = periodUpdatePosMs * 0.001;
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
        posLeftDpx = (w - ballWidth)/2;
        posTopDpx = (h - ballHeight)/2;

        // Velocity of the ball = 0
        xSpeedPixSec = 0;
        ySpeedPixSec = 0;
    }

    // onDraw is called by the system each time the view component is displayed or updated
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Drawing the picture in the middle of the view component
        canvas.drawBitmap(ballPicture, (int)posLeftDpx, (int)posTopDpx, picturePainter);
    }

    // performClick method for a better accessibility
    @Override
    public boolean performClick(){
        super.performClick();

        // The old view redraws with the new view (onDraw method is called)
        invalidate();
        return true;
    }
}