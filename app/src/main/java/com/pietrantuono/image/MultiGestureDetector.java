package com.pietrantuono.image;

import android.util.Log;
import android.view.MotionEvent;

import hugo.weaving.DebugLog;

public class MultiGestureDetector {
    private static final int INVALID_POINTER_ID = -1;
    private  final String TAG = getClass().getSimpleName();
    private Pointer pointerOne;
    private Pointer pointerZero;
    private float mAngle;
    private OnRotationGestureListener mListener;
    private boolean isRotating;
    private boolean isScaling;
    private Angle angle;

    public float getAngle() {
        return mAngle;
    }

    public MultiGestureDetector(OnRotationGestureListener listener) {
        mListener = listener;
        pointerZero = new Pointer(Pointer.ID_POINTER_ZERO);
        pointerOne = new Pointer(Pointer.ID_POINTER_ONE);
        angle=new Angle();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                updatePointers(event);
                updatePointersPosition(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "ACTION_POINTER_DOWN ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                updatePointers(event);
                updatePointersPosition(event);
                break;
            case MotionEvent.ACTION_MOVE:
                updatePointersPosition(event);
                angle.calulcateAndSet(pointerZero,pointerOne);
                Log.d(TAG, "ACTION_MOVE ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                if(isTranslate(event)){
                    onTranslate(getTranslationX(event), getTranslationY(event));
                    return true;
                }
                if(isRotation(event)){
                    onRotate(getAngle(event), getCenterX(event), getCenterY(event));
                    return true;
                }
                if(isScale(event)){
                    onScale(getScale(event));
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                updatePointers(event);
                updatePointersPosition(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "ACTION_POINTER_UP ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                updatePointers(event);
                updatePointersPosition(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "ACTION_CANCEL ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                cancel(event);
                updatePointersPosition(event);
                break;
        }
        return true;
    }

    private void updatePointersPosition(MotionEvent event) {
        if(pointerZero.isValid()) pointerZero.setCurrentX(event.getX(event.findPointerIndex(pointerZero.getPointerId())));
        if(pointerOne.isValid()) pointerOne.setCurrentX(event.getX(event.findPointerIndex(pointerOne.getPointerId())));

        if(pointerZero.isValid()) pointerZero.setCurrentY(event.getY(event.findPointerIndex(pointerZero.getPointerId())));
        if(pointerOne.isValid()) pointerOne.setCurrentY(event.getY(event.findPointerIndex(pointerOne.getPointerId())));
        Log.d(TAG,pointerZero.toString());
        Log.d(TAG,pointerOne.toString());
    }

    @DebugLog
    private void updatePointers(MotionEvent event) {
        int pointerID = event.getPointerId(event.getActionIndex());
        int eventType = event.getActionMasked();
        if(eventType==MotionEvent.ACTION_DOWN){
            if(pointerID==Pointer.ID_POINTER_ZERO){
                pointerZero.setValid();
                pointerZero.clear();
            }
            if(pointerID==Pointer.ID_POINTER_ONE) {
                pointerOne.setValid();
                pointerOne.clear();
            }
        }
        if(eventType==MotionEvent.ACTION_POINTER_DOWN){
            if(pointerID==Pointer.ID_POINTER_ONE) {
                pointerOne.setValid();
                pointerOne.clear();
            }
            if(pointerID==Pointer.ID_POINTER_ZERO){
                pointerZero.setValid();
                pointerZero.clear();
            }
        }
        if(eventType==MotionEvent.ACTION_POINTER_UP){
            if(pointerID==Pointer.ID_POINTER_ONE) {
                pointerOne.setInvalid();
                pointerOne.clear();
            }
            if(pointerID==Pointer.ID_POINTER_ZERO){
                pointerZero.setInvalid();
                pointerZero.clear();
            }
        }
        if(eventType==MotionEvent.ACTION_UP){
            if(pointerID==Pointer.ID_POINTER_ZERO){
                pointerZero.setInvalid();
                pointerZero.clear();
            }
            if(pointerID==Pointer.ID_POINTER_ONE) {
                pointerOne.setInvalid();
                pointerOne.clear();
            }
        }
    }


    private boolean isTranslate(MotionEvent event) {
        return pointerZero.isValid() && !pointerOne.isValid();
    }
    @DebugLog
    private void onScale(float scale) {

    }

    private float getScale(MotionEvent event) {
        return 0;
    }

    private boolean isScale(MotionEvent event) {
        if(!pointerOne.isValid() || !pointerZero.isValid())return false;
        return true;
    }
    @DebugLog
    private void onRotate(float angle, float centerX, float centerY) {

    }

    private float getCenterY(MotionEvent event) {
        return 0;

    }

    private float getCenterX(MotionEvent event) {
        return 0;
    }

    private float getAngle(MotionEvent event) {
        return 0;
    }

    private boolean isRotation(MotionEvent event) {
        if(!pointerOne.isValid() || !pointerZero.isValid())return false;
        return true;
    }
    @DebugLog
    private void onTranslate(float translationX, float translationY) {

    }

    private float getTranslationY(MotionEvent event) {
        return 0;
    }

    private float getTranslationX(MotionEvent event) {
        return 0;
    }

    private void cancel(MotionEvent event) {


    }




    private void onEndRotation() {

    }

    private void onStartRotation() {

    }



    public static interface OnRotationGestureListener {
        public void onRotation(MultiGestureDetector rotationDetector);

        public void onEndRotation();

        public void onStartRotation();
    }
} 