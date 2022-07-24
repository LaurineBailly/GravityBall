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

public class BallView extends View {

    // Pencil that will allow the picture to be drawn
    private Paint picturePainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Bitmap that will be drawn
    private Bitmap ballPicture;

    // Picture postion from left and top of the view component
    private int posTopDpx;
    private int posLeftDpx;

    // Old picture postion from left and top of the view component
    private int previousPosTopDpx;
    private int previousPosLeftDpx;

    // Component view constructor with the physical layout
    public BallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // Component view constructor without the physical layout
    public BallView(Context context) {
        super(context);
    }

    // Set new position values of the ball
    public void setPosition(double accelerationAx, double accelerationAy, double timerPeriodSeconds, double onePixelInMm) {

        // Variables stocking view and ballview graphic properties
        int viewTop = getTop();
        int viewBottom = getBottom();
        int viewLeft = getLeft();
        int viewRight = getRight();
        int ballHeight = ballPicture.getHeight();
        int ballWidth = ballPicture.getWidth();

        // Position calculation in pixels after acceleration
        // s = u*t + (1/2)a t^2
        //where s is position, u is velocity at t=0, t is time and a is a constant acceleration.
        int newPosX = (int)(2*posLeftDpx - previousPosLeftDpx + 0.5*accelerationAx*timerPeriodSeconds*timerPeriodSeconds);
        int newPosY = (int)(2*posTopDpx - previousPosTopDpx + 0.5 *accelerationAy*timerPeriodSeconds*timerPeriodSeconds);

        // Aging the values of posLeftDpx and posTopDpx
        previousPosTopDpx = posTopDpx;
        previousPosLeftDpx = posLeftDpx;

        // Determining the Y position value

        // If the ball reachs the Top of the screen, the ball does not get out of the screen
        if(newPosY < viewTop) {
            this.posTopDpx = viewTop;
        }

        // If the ball reachs the bottom of the screen, the ball does not get out of the screen
        else if(newPosY > (viewBottom - ballHeight)) {
            this.posTopDpx = viewBottom - ballHeight;
        }
        else {
            this.posTopDpx = newPosY;
        }

        // Determining the X position value

        // If the ball reachs the left of the screen, the ball does not get out of the screen
        if(newPosX < viewLeft) {
            this.posLeftDpx = viewLeft;
        }

        // If the ball reachs the right of the screen, the ball does not get out of the screen
        else if(newPosX > (viewRight - ballWidth)) {
            this.posLeftDpx = viewRight - ballWidth;
        }
        else {
            this.posLeftDpx = newPosX;
        }
    }

    public int getPosTopDpx() {
        return posTopDpx;
    }

    public int getPosLeftDpx() {
        return posLeftDpx;
    }

    // onSizeChanged is called each time the size view changes, here only once because the activity
    // in which the view is displayed has been stacked in protrait mode (see manifest file).
    // onSizeChanged is called before onDraw.
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        // Loading picture into bitmap
        ballPicture = BitmapFactory.decodeResource(getResources(), R.drawable.bille);

        // Getting left and top position for a picture at the center of the view component
        posLeftDpx = (w - ballPicture.getWidth())/2;
        previousPosLeftDpx = posLeftDpx;
        posTopDpx = (h - ballPicture.getHeight())/2;
        previousPosTopDpx = posTopDpx;
    }

    // onDraw is called by the system each time the view component is displayed or updated
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Drawing the picture in the middle of the view component
        canvas.drawBitmap(ballPicture, posLeftDpx, posTopDpx, picturePainter);
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