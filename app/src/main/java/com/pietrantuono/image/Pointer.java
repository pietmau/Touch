package com.pietrantuono.image;

import android.support.annotation.IntDef;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class Pointer {
    @PointerId int pointerId;
    private boolean isValid;

    private float previousX;
    private float previousY;

    private float currentX;
    private float currentY;

    public static final int ID_POINTER_ONE = 1;
    public static final int ID_POINTER_ZERO = 0;

    public Pointer(@PointerId int pointerId) {
        this.pointerId = pointerId;
    }

    public void setCurrentX(float currentX) {
        this.previousX=this.currentX;
        this.currentX = currentX;
    }

    public void setCurrentY(float currentY) {
        this.previousY=this.currentY;
        this.currentY = currentY;
    }

    public void setValid() {
        isValid=true;
    }
    public void setInvalid() {
        isValid=false;
    }

    public boolean isValid(){
        return isValid;
    }

    public void clear() {
        previousX =0;
        previousY =0;
        currentX=0;
        currentY=0;
    }

    public int getPointerId() {
        return this.pointerId;
    }

    @IntDef({ID_POINTER_ONE,ID_POINTER_ZERO})
    public @interface PointerId {
    }

    @Override
    public String toString() {
        return "Pointer"+pointerId +": "+ "Current X="+currentX+" Current Y="+currentY+" Previous X="+previousX+" Previous Y="+previousY;

    }

    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public float getPreviousX() {
        return previousX;
    }

    public float getPreviousY() {
        return previousY;
    }
}
