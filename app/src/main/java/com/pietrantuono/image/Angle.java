package com.pietrantuono.image;

import android.util.Log;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class Angle {
    private  final String TAG =getClass().getSimpleName();
    private float currentAngle;
    private float previousAngle;
    private float currentPivotX;
    private float currentPivotY;
    private final static float INVALID_ANGLE=-720;


    public Angle() {
        reset();
    }

    public Angle calulcateAndSet(Pointer pointerZero, Pointer pointerOne) {
        if (!pointerOne.isValid() || !pointerOne.isValid()) {
            reset();
            return this;
        }
        previousAngle=currentAngle;
        currentPivotX=pointerZero.getCurrentX()+(pointerOne.getCurrentX()-pointerZero.getCurrentX());
        currentPivotY=pointerZero.getCurrentY()+(pointerOne.getCurrentY()-pointerZero.getCurrentY());
        float angle1 = (float) Math.atan2((pointerZero.getCurrentY() - pointerOne.getCurrentY()), (pointerZero.getCurrentX() - pointerOne.getCurrentX()));
        //float angle = ((float) Math.toDegrees(angle1) % 360);
        float angle = (float) Math.toDegrees(angle1);
//        if(angle < 0){
//            angle += 360;
//        }
        Log.d(TAG, "Calcualted angle "+angle);
        currentAngle = angle;
        if (angle < -180.f) currentAngle += 360.0f;
        if (angle > 180.f) currentAngle -= 360.0f;
        return this;
    }

    @Override
    public String toString() {
        return "" + currentAngle;
    }

    public void reset() {
        currentAngle=INVALID_ANGLE;
        previousAngle=INVALID_ANGLE;
        currentPivotX=0;
        currentPivotY=0;
    }

    public float getCurrentAngle() {
        return currentAngle;
    }

    public float getCurrentPivotX() {
        return currentPivotX;
    }

    public float getCurrentPivotY() {
        return currentPivotY;
    }
    public float getDeltaAngle(){
        if(previousAngle==INVALID_ANGLE || currentAngle==INVALID_ANGLE)return 0;
        return currentAngle-previousAngle;
    }
}


