package com.pietrantuono.image;

import android.util.Log;
import android.view.MotionEvent;

public class RotationGestureDetector {
    private static final int INVALID_POINTER_ID = -1;
    private  final String TAG = getClass().getSimpleName();
    private float fX, fY, sX, sY;
    private int ptrID1, ptrID2;
    private float mAngle;

    private OnRotationGestureListener mListener;
    private boolean rotationSrtarted;

    public float getAngle() {
        return mAngle;
    }

    public RotationGestureDetector(OnRotationGestureListener listener) {
        mListener = listener;
        ptrID1 = INVALID_POINTER_ID;
        ptrID2 = INVALID_POINTER_ID;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                ptrID1 = event.getPointerId(event.getActionIndex());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "ACTION_POINTER_DOWN ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                ptrID2 = event.getPointerId(event.getActionIndex());
                onStartRotation();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "ACTION_MOVE ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                if (ptrID1 != INVALID_POINTER_ID && ptrID2 != INVALID_POINTER_ID) {
                    float nfX, nfY, nsX, nsY;
                    nsX = event.getX(event.findPointerIndex(ptrID1));
                    nsY = event.getY(event.findPointerIndex(ptrID1));
                    nfX = event.getX(event.findPointerIndex(ptrID2));
                    nfY = event.getY(event.findPointerIndex(ptrID2));

                    mAngle = angleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY);

                    sX = nsX;
                    sY = nsY;
                    fX = nfX;
                    fY = nfY;
                    if (mListener != null) {
                        mListener.onRotation(this);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                ptrID1 = INVALID_POINTER_ID;
                onEndRotation();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "ACTION_POINTER_UP ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                ptrID2 = INVALID_POINTER_ID;
                onEndRotation();
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "ACTION_CANCEL ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                ptrID1 = INVALID_POINTER_ID;
                ptrID2 = INVALID_POINTER_ID;
                onEndRotation();
                break;
        }
        return true;
    }

    private void onEndRotation() {
        if (rotationSrtarted) mListener.onEndRotation();
        rotationSrtarted = false;
    }

    private void onStartRotation() {
        mListener.onStartRotation();
        rotationSrtarted = true;
    }

    private float angleBetweenLines(float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY) {
        float angle1 = (float) Math.atan2((fY - sY), (fX - sX));
        float angle2 = (float) Math.atan2((nfY - nsY), (nfX - nsX));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return angle;
    }

    public static interface OnRotationGestureListener {
        public void onRotation(RotationGestureDetector rotationDetector);

        public void onEndRotation();

        public void onStartRotation();
    }
} 