package com.pietrantuono.image;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class Angle {
    private float currentAngle;
    private float previousAngle;
    private float currentPivotX;
    private float currentPivotY;

    public Angle calulcateAndSet(Pointer pointerZero, Pointer pointerOne) {
        if (!pointerOne.isValid() || !pointerOne.isValid()) {
            reset();
            return this;
        }
        currentAngle = previousAngle;
        currentPivotX=pointerOne.getCurrentX()-pointerZero.getCurrentX();
        currentPivotY=pointerOne.getCurrentY()-pointerZero.getCurrentY();
        float angle1 = (float) Math.atan2((pointerZero.getCurrentY() - pointerOne.getCurrentY()), (pointerZero.getCurrentX() - pointerOne.getCurrentX()));
        float angle = ((float) Math.toDegrees(angle1) % 360);
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
        currentAngle=0;
        previousAngle=0;
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
}


