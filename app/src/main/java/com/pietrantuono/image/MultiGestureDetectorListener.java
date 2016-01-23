package com.pietrantuono.image;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public interface MultiGestureDetectorListener {
    void onTranslate(float translationX, float translationY);

    void onRotate(float deltaAngle, float currentPivotX, float currentPivotY);

    void onScale(float deltaScale, float currentPivotX, float currentPivotY);

    void onRotationEnd();

}
