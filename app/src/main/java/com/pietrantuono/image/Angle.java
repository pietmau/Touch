package com.pietrantuono.image;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class Angle {
    float currentAngle;

    public void calulcateAndSet(Pointer pointerZero, Pointer pointerOne) {
        if(!pointerOne.isValid() || !pointerOne.isValid())return;
        float angle1 = (float) Math.atan2((pointerZero.getCurrentY() - pointerOne.getCurrentY()), (pointerZero.getCurrentX() - pointerOne.getCurrentX()));
        float angle = ((float) Math.toDegrees(angle1) % 360);
        if (angle < -180.f) currentAngle += 360.0f;
        if (angle > 180.f) currentAngle -= 360.0f;
    }

    @Override
    public String toString() {
        return ""+currentAngle;
    }

    public void clear(){currentAngle=0;}
}


