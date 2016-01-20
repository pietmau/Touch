package com.pietrantuono.image;

import android.util.Log;
import android.view.MotionEvent;

public class PanGestureDetector {
    private static final int INVALID = -1;
    private final String TAG = getClass().getSimpleName();
    private float fX, fY, sX, sY;
    private int pointer;
    private boolean moveStarted;
    private OnMoveGestureListener mListener;
    private float initialX;
    private float initialY;
    private float distanceX;
    private float distanceY;

    public PanGestureDetector(OnMoveGestureListener listener) {
        mListener = listener;
        pointer = INVALID;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                pointer = event.getPointerId(event.getActionIndex());
                initialX = event.getX(event.findPointerIndex(pointer));
                initialY = event.getY(event.findPointerIndex(pointer));
                onStartMove();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "ACTION_POINTER_DOWN ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                if (event.getPointerId(event.getActionIndex()) != pointer) {
                    pointer = INVALID;
                    onEndMove();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "ACTION_MOVE ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                if (pointer != INVALID && event.getPointerId(event.getActionIndex())== pointer) {
                    float newX = event.getX();
                    float newY = event.getY();

                    distanceX = getDistanceX(initialX, newX);
                    distanceY= getDistanceX(initialY,newY);
                    initialX = newX;
                    initialY = newY;

                    onMove(distanceX,distanceY);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                if (event.getPointerId(event.getActionIndex()) != pointer) {
                    pointer = INVALID;
                    onEndMove();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "ACTION_CANCEL ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                if (event.getPointerId(event.getActionIndex()) != pointer) {
                    pointer = INVALID;
                    onEndMove();
                }
                break;
        }
        return true;
    }

    private float getDistanceX(float initialX, float newX) {
        return initialX-newX;
    }

    private float getDistanceY(float initialY, float newY) {
        return initialY-newY;
    }

    private void onMove(float distanceX, float distanceY) {
        mListener.onMove(distanceX,distanceY);
    }

    private void onEndMove() {
        if (moveStarted) mListener.onEndMove();
        moveStarted = false;
    }

    private void onStartMove() {
        mListener.onStartMove();
        moveStarted = true;
    }

    private float angleBetweenLines(float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY) {
        float angle1 = (float) Math.atan2((fY - sY), (fX - sX));
        float angle2 = (float) Math.atan2((nfY - nsY), (nfX - nsX));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return angle;
    }

    public interface OnMoveGestureListener {
        void onMove(float distance, float distanceY);

        void onEndMove();

        void onStartMove();
    }
} 