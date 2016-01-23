package com.pietrantuono.image;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class Scale {
    private float pointerZeroCurrentX;
    private float pointerZeroCurrentY;

    private float pointerOneCurrentX;
    private float pointerOneCurrentY;

    private float currentDistance;

    private float currentScale;
    private float previousScale;

    private float initialDistance;
    private float currentPivotY;
    private float currentPivotX;

    public Scale calcuateAndSet(Pointer pointerZero, Pointer pointerOne) {
        if (!pointerOne.isValid() || !pointerOne.isValid()) {
            reset();
            return this;
        }

        pointerOneCurrentX = pointerOne.getCurrentX();
        pointerOneCurrentY = pointerOne.getCurrentY();
        pointerZeroCurrentX = pointerZero.getCurrentX();
        pointerZeroCurrentY = pointerZero.getCurrentY();

        currentDistance = calculateDistance();
        if (initialDistance == 0) initialDistance = currentDistance;
        previousScale = currentScale;
        currentScale = calculateScale();

        currentPivotX = pointerZero.getCurrentX() + (pointerOne.getCurrentX() - pointerZero.getCurrentX());
        currentPivotY = pointerZero.getCurrentY() + (pointerOne.getCurrentY() - pointerZero.getCurrentY());

        return this;
    }

    private float calculateScale() {
        return currentDistance / initialDistance;
    }

    private float calculateDistance() {
        double x = Math.pow((pointerOneCurrentX - pointerZeroCurrentX), 2);
        double y = Math.pow((pointerOneCurrentY - pointerZeroCurrentY), 2);
        return (float) Math.sqrt(x + y);
    }

    @Override
    public String toString() {
        return " Distance =" + currentDistance + ", Scale= " + currentScale;
    }

    public void reset() {
        pointerZeroCurrentX = 0;
        pointerZeroCurrentY = 0;

        pointerOneCurrentX = 0;
        pointerOneCurrentY = 0;

        currentDistance = 0;

        currentScale = 0;
    }

    public float getCurrentPivotX() {
        return currentPivotX;
    }

    public float getCurrentPivotY() {
        return currentPivotY;
    }

    public float getScaleDelta() {
        if (previousScale == 0) return 1;
        return 1 + (currentScale - previousScale);
    }
}


