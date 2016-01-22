package com.pietrantuono.image;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class Scale {
    private float pointerZeroCurrentX;
    private float pointerZeroCurrentY;
    private float pointerZeroPreviousX;
    private float pointerZeroPreviousY;

    private float pointerOneCurrentX;
    private float pointerOneCurrentY;
    private float pointerOnePreviousX;
    private float pointerOnePreviousY;
    
    private float currentDistance;
    private float previousDistance;

    private float currentScale;

    private float initialDistance;

    public Scale calcuateAndSet(Pointer pointerZero, Pointer pointerOne) {
        if (!pointerOne.isValid() || !pointerOne.isValid()) {
            reset();
            return this;
        }
        pointerOnePreviousX=pointerOneCurrentX;
        pointerOnePreviousY=pointerOneCurrentY;
        pointerZeroPreviousX=pointerZeroCurrentX;
        pointerOnePreviousY=pointerZeroCurrentY;

        pointerOneCurrentX=pointerOne.getCurrentX();
        pointerOneCurrentY=pointerOne.getCurrentY();
        pointerZeroCurrentX=pointerZero.getCurrentX();
        pointerZeroCurrentY=pointerZero.getCurrentY();

        previousDistance=currentDistance;
        currentDistance=calculateDistance();
        if(initialDistance==0)initialDistance=currentDistance;
        currentScale=calculateScale();

        return this;
    }

    private float calculateScale() {
        return currentDistance/initialDistance;

    }

    private float calculateDistance() {
        double x = Math.pow((pointerOneCurrentX - pointerZeroCurrentX), 2);
        double y = Math.pow((pointerOneCurrentY - pointerZeroCurrentY), 2);
        return (float) Math.sqrt(x+y);
    }

    @Override
    public String toString() {
        return " Distance =" + currentDistance+", Scale= "+currentScale;
    }

    public void reset() {
         pointerZeroCurrentX=0;
         pointerZeroCurrentY=0;
         pointerZeroPreviousX=0;
         pointerZeroPreviousY=0;

         pointerOneCurrentX=0;
         pointerOneCurrentY=0;
         pointerOnePreviousX=0;
         pointerOnePreviousY=0;

         currentDistance=0;
         previousDistance=0;

         currentScale=0;
    }

    public float getCurrentScale() {
        return currentScale;
    }
}


