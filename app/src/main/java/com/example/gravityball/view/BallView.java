package com.example.gravityball.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
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

    // Number of time the ball is at the bottom of the view
    private int hitsOnBottom = 0;

    // Component view constructor with the physical layout
    public BallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // Component view constructor without the physical layout
    public BallView(Context context) {
        super(context);
    }

    public void setPosition(int posTopDpx, int posLeftDpx) {

        int viewTop = getTop();
        int viewBottom = getBottom();
        int viewLeft = getLeft();
        int viewRight = getRight();
        int ballHight = ballPicture.getHeight();
        int ballWidth = ballPicture.getWidth();


        // Determining the X position value

        // If the finger reachs the Top of the screen, the ball does not get out of the screen
        if(posTopDpx < viewTop + ballHight/2) {
            this.posTopDpx = viewTop;
            hitsOnBottom = 0;
        }

        // If the finger reachs the bottom of the screen, the ball does not get out of the screen
        else if(posTopDpx > viewBottom - ballHight/2) {
            this.posTopDpx = viewBottom - ballHight;
            hitsOnBottom++;
        }
        else {
            this.posTopDpx = posTopDpx - ballHight/2;
            hitsOnBottom = 0;
        }

        // Determining the Y position value

        // If the finger reachs the left of the screen, the ball does not get out of the screen
        if(posLeftDpx < viewLeft + ballWidth/2) {
            this.posLeftDpx = viewLeft;
        }

        // If the finger reachs the right of the screen, the ball does not get out of the screen
        else if(posLeftDpx > viewRight - ballWidth/2) {
            this.posLeftDpx = viewRight - ballWidth;
        }
        else {
            this.posLeftDpx = posLeftDpx - ballWidth/2;
        }
    }

    public int getPosTopDpx() {
        return posTopDpx + ballPicture.getHeight()/2;
    }

    public int getPosLeftDpx() {
        return posLeftDpx + ballPicture.getWidth()/2;
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
        posTopDpx = (h - ballPicture.getHeight())/2;
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

        // If the user does not try to browse the ball from outside the view
        if(hitsOnBottom <= 1) {

            // The old view redraws with the new view (onDraw method is called)
            invalidate();
            return true;
        }
        else {
            return false;
        }
    }
}