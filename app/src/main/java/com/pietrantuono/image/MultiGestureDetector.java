package com.pietrantuono.image;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import hugo.weaving.DebugLog;

public class MultiGestureDetector {
    private  final String TAG = getClass().getSimpleName();
    private Pointer pointerOne;
    private Pointer pointerZero;
    private Angle angle;
    private Scale scale;
    private MultiGestureDetectorListener listener;
    private ImageView imageView;

    public MultiGestureDetector(MultiGestureDetectorListener listener, ImageView imageView) {
        this.listener = listener;
        this.imageView = imageView;
        pointerZero = new Pointer(Pointer.ID_POINTER_ZERO);
        pointerOne = new Pointer(Pointer.ID_POINTER_ONE);
        angle=new Angle();
        scale =new Scale();
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
                updatePointers(event);
                updatePointersPosition(event);
                if(!eventIsOnBitmap(event))break;
                updateGeometry();

                Log.d(TAG, "ACTION_MOVE ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                Log.d(TAG, "Angle = " + angle.toString());
                Log.d(TAG, "" + scale.toString());

                if(isTranslate()){
                    if(listener!=null)listener.onTranslate(getTranslationX(), getTranslationY());
                }
                if(isRotation()){
                    if(listener!=null)listener.onRotate(angle.getDeltaAngle(), angle.getCurrentPivotX(), angle.getCurrentPivotY());
                }
                if(isScale()){
                    if(listener!=null)listener.onScale(scale.getScaleDelta(),scale.getCurrentPivotX(),scale.getCurrentPivotY());
                }

                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                updatePointers(event);
                updatePointersPosition(event);
                updateGeometry();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "ACTION_POINTER_UP ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                updatePointers(event);
                updatePointersPosition(event);
                if(listener!=null)listener.onRotationEnd();
               updateGeometry();
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "ACTION_CANCEL ,pointer ID = " + event.getPointerId(event.getActionIndex()));
                updatePointers(event);
                updatePointersPosition(event);
                updateGeometry();
                if(listener!=null)listener.onRotationEnd();
                break;
        }
        return true;
    }

    private void updateGeometry() {
        angle.calulcateAndSet(pointerZero, pointerOne);
        scale.calcuateAndSet(pointerZero, pointerOne);

    }

    private void updatePointersPosition(MotionEvent event) {
        if(pointerZero.isValid()) pointerZero.setCurrentX(event.getX(event.findPointerIndex(pointerZero.getPointerId())));
        if(pointerOne.isValid()) pointerOne.setCurrentX(event.getX(event.findPointerIndex(pointerOne.getPointerId())));

        if(pointerZero.isValid()) pointerZero.setCurrentY(event.getY(event.findPointerIndex(pointerZero.getPointerId())));
        if(pointerOne.isValid()) pointerOne.setCurrentY(event.getY(event.findPointerIndex(pointerOne.getPointerId())));
        Log.d(TAG, pointerZero.toString());
        Log.d(TAG, pointerOne.toString());
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


    private boolean isTranslate() {
        return pointerZero.isValid() && !pointerOne.isValid();
    }

    private boolean isScale() {
        if(!pointerOne.isValid() || !pointerZero.isValid())return false;
        return true;
    }

    private boolean isRotation() {
        if(!pointerOne.isValid() || !pointerZero.isValid())return false;
        return true;
    }

    private float getTranslationY() {
        return -(pointerZero.getCurrentY()-pointerZero.getPreviousY());
    }

    private float getTranslationX() {
        return -(pointerZero.getCurrentX()-pointerZero.getPreviousX());
    }


    private boolean eventIsOnBitmap(MotionEvent event) {
        Matrix matrix = imageView.getImageMatrix();
        Matrix temp = new Matrix();
        temp.set(matrix);
        RectF rectFSource = new RectF();
        RectF rectFDestination = new RectF();
        rectFSource.top = 0;
        rectFSource.left = 0;
        rectFSource.right = imageView.getDrawable().getIntrinsicWidth();
        rectFSource.bottom = imageView.getDrawable().getIntrinsicHeight();
        Matrix imagemaxtrix = imageView.getImageMatrix();//;
        Matrix matrix1 = new Matrix();
        matrix1.set(imagemaxtrix);
        matrix1.mapRect(rectFDestination, rectFSource);
        return rectFDestination.contains(event.getX(), event.getY());

    }
} 