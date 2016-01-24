package com.pietrantuono.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

import hugo.weaving.DebugLog;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class ViewFinder extends ImageView {
    private static final float ASPECT_RATIO=1819/1382f;
    private float previousAngle;
    private MultiGestureDetector multiGestureDetector;

    public ViewFinder(Context context) {
        super(context);
    }

    public ViewFinder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewFinder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ViewFinder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getDrawable() != null) multiGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        float imageWidth = bm.getWidth();
        float imageHeight = bm.getHeight();
        RectF drawableRect = new RectF(0, 0, imageWidth, imageHeight);
        RectF viewRect = new RectF(0, 0, getWidth(), getHeight());

        if (drawableRect.width() > drawableRect.height())
        //Bitmap is landscape
        {
            float scale = viewRect.height() / drawableRect.height();
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            RectF destination = new RectF();
            matrix.mapRect(destination, drawableRect);
            matrix.postTranslate(-destination.width() / 2 + viewRect.width() / 2, 0);
            setImageMatrix(matrix);
        }
        //Bitmap is portrait
        else {
            float scale = viewRect.width() / drawableRect.width();
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            setImageMatrix(matrix);
        }
        initDedetector();
    }


    private void initDedetector() {
        multiGestureDetector = new MultiGestureDetector(new MultiGestureDetectorListener() {
            @Override
            public void onTranslate(float translationX, float translationY) {
                translateImage(translationX, translationY);
            }

            @Override
            public void onRotate(float deltaAngle, float currentPivotX, float currentPivotY) {
                rotateImage(deltaAngle);
            }

            @Override
            public void onScale(float deltaScale, float currentPivotX, float currentPivotY) {
                scaleImage(deltaScale, currentPivotX, currentPivotY);
            }

            @Override
            public void onRotationEnd() {
                onEndRotation();
            }
        }, this);
    }


    @DebugLog
    private void translateImage(float distanceX, float distanceY) {
        Matrix matrix = new Matrix();
        matrix.set(getImageMatrix());
        matrix.postTranslate(-distanceX, -distanceY);
        setImageMatrix(matrix);
    }

    @DebugLog
    private void scaleImage(float scaleFactor, float focusX, float focusY) {
        Matrix matrix = getImageMatrix();
        Matrix displayMatrix = new Matrix();
        displayMatrix.set(matrix);
        displayMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
        setImageMatrix(displayMatrix);
    }

    public void saveImage() {
        int x = getImageX();
        int y = getImageY();
        int width = getImageWidth();
        int height = getImageHeigth();
        Matrix matrix = new Matrix();
        matrix.set(getImageMatrix());
        Bitmap notRotatedbitmap = Bitmap.createBitmap(((BitmapDrawable) getDrawable()).getBitmap(), 0, 0, ((BitmapDrawable) getDrawable()).getBitmap().getWidth(), ((BitmapDrawable) getDrawable()).getBitmap().getHeight(), matrix, true);
        Bitmap croppedbitmap = Bitmap.createBitmap(notRotatedbitmap, x, y, width, height);
        String root = Environment.getExternalStorageDirectory().toString();
        String fname = "Touchnote.jpg";
        File file = new File(root, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            croppedbitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            croppedbitmap.recycle();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getImageHeigth() {
        Matrix matrix = getImageMatrix();
        Matrix temp = new Matrix();
        temp.set(matrix);
        RectF rectFSource = new RectF();
        RectF rectFDestination = new RectF();
        rectFSource.top = 0;
        rectFSource.left = 0;
        rectFSource.right = getDrawable().getIntrinsicWidth();
        rectFSource.bottom = getDrawable().getIntrinsicHeight();
        Matrix imagemaxtrix = getImageMatrix();
        Matrix matrix1 = new Matrix();
        matrix1.set(imagemaxtrix);
        matrix1.mapRect(rectFDestination, rectFSource);

        RectF imageviewRetf = new RectF();
        imageviewRetf.top = 0;
        imageviewRetf.left = 0;
        imageviewRetf.right = getWidth();
        imageviewRetf.bottom = getHeight();

        RectF intersection = new RectF();
        intersection.set(imageviewRetf);

        if (intersection.intersect(rectFDestination)) {
            return (int) (intersection.bottom - intersection.top);
        } else return 0;
    }

    private int getImageWidth() {
        RectF rectFSource = new RectF();
        RectF rectFDestination = new RectF();
        rectFSource.top = 0;
        rectFSource.left = 0;
        rectFSource.right = getDrawable().getIntrinsicWidth();
        rectFSource.bottom = getDrawable().getIntrinsicHeight();
        Matrix imagemaxtrix = getImageMatrix();//;
        Matrix matrix1 = new Matrix();
        matrix1.set(imagemaxtrix);
        matrix1.mapRect(rectFDestination, rectFSource);

        RectF imageviewRetf = new RectF();
        imageviewRetf.top = 0;
        imageviewRetf.left = 0;
        imageviewRetf.right = getWidth();
        imageviewRetf.bottom = getHeight();

        RectF intersection = new RectF();
        intersection.set(imageviewRetf);

        if (intersection.intersect(rectFDestination)) {
            return (int) (intersection.right - intersection.left);
        } else return 0;
    }

    private int getImageY() {
        RectF rectFSource = new RectF();
        RectF rectFDestination = new RectF();
        rectFSource.top = 0;
        rectFSource.left = 0;
        rectFSource.right = getDrawable().getIntrinsicWidth();
        rectFSource.bottom = getDrawable().getIntrinsicHeight();
        Matrix imagemaxtrix = getImageMatrix();
        Matrix matrix1 = new Matrix();
        matrix1.set(imagemaxtrix);
        matrix1.mapRect(rectFDestination, rectFSource);

        RectF imageviewRetf = new RectF();
        imageviewRetf.top = 0;
        imageviewRetf.left = 0;
        imageviewRetf.right = getWidth();
        imageviewRetf.bottom = getHeight();

        RectF modified = new RectF();
        modified.set(imageviewRetf);

        if (modified.intersect(rectFDestination)) {
            modified.offset(-rectFDestination.left, -rectFDestination.top);
            return (int) modified.top;
        } else return 0;

    }

    public int getImageX() {
        RectF rectFSource = new RectF();
        RectF rectFDestination = new RectF();
        rectFSource.top = 0;
        rectFSource.left = 0;
        rectFSource.right = getDrawable().getIntrinsicWidth();
        rectFSource.bottom = getDrawable().getIntrinsicHeight();
        Matrix imagemaxtrix = getImageMatrix();//;
        Matrix matrix1 = new Matrix();
        matrix1.set(imagemaxtrix);

        matrix1.mapRect(rectFDestination, rectFSource);

        RectF imageviewRetf = new RectF();
        imageviewRetf.top = 0;
        imageviewRetf.left = 0;
        imageviewRetf.right = getWidth();
        imageviewRetf.bottom = getHeight();

        RectF modified = new RectF();
        modified.set(imageviewRetf);

        if (modified.intersect(rectFDestination)) {
            modified.offset(-rectFDestination.left, -rectFDestination.top);
            return (int) modified.left;
        } else return 0;
    }

    @DebugLog
    public void rotateImage(float currentDeltaAngle) {
        Matrix matrix = getImageMatrix();
        Matrix rotateMatrixx = new Matrix();
        rotateMatrixx.set(matrix);
        matrix = getImageMatrix();
        Matrix displayMatrix = new Matrix();
        displayMatrix.set(matrix);
        displayMatrix.postRotate(currentDeltaAngle, getImageViewCenterX(), getImageViewCenterY());
        setImageMatrix(displayMatrix);
        previousAngle += currentDeltaAngle;
    }

    @DebugLog
    public void onEndRotation() {
        float angle = previousAngle;
        float snapAngle = 0;
        if (angle < 45 && angle >= 0) snapAngle = 0;
        else if (angle < 135 && angle >= 45) snapAngle = 90;
        else if (angle <= 180 && angle >= 135) snapAngle = 180;
        else if (angle <= 0 && angle > -45) snapAngle = 0;
        else if (angle <= -45 && angle > -135) snapAngle = -90;
        else if (angle <= -138 && angle >= -180) snapAngle = -180;
        Matrix matrix = getImageMatrix();
        Matrix rotateMatrixx = new Matrix();
        rotateMatrixx.set(matrix);
        rotateMatrixx.postRotate(-previousAngle, getImageViewCenterX(), getImageViewCenterY());
        setImageMatrix(rotateMatrixx);
        matrix = getImageMatrix();
        Matrix displayMatrix = new Matrix();
        displayMatrix.set(matrix);
        displayMatrix.postRotate(snapAngle, getImageViewCenterX(), getImageViewCenterY());
        setImageMatrix(displayMatrix);
        previousAngle = 0;
    }

    private float getImageViewCenterX() {
        RectF imageviewRetf = new RectF();
        imageviewRetf.top = 0;
        imageviewRetf.left = 0;
        imageviewRetf.right = getWidth();
        imageviewRetf.bottom = getHeight();
        return imageviewRetf.centerX();
    }

    private float getImageViewCenterY() {
        RectF imageviewRetf = new RectF();
        imageviewRetf.top = 0;
        imageviewRetf.left = 0;
        imageviewRetf.right = getWidth();
        imageviewRetf.bottom = getHeight();
        return imageviewRetf.centerY();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable stateOfSuper=super.onSaveInstanceState();
        ViewFinderSavedState viewFinderSavedState = new ViewFinderSavedState(stateOfSuper);
        try {
            viewFinderSavedState.matrix = getImageMatrix();
        } catch (Exception e){}
        if(getDrawable()!=null)viewFinderSavedState.bitmap=((BitmapDrawable)getDrawable()).getBitmap();
        return viewFinderSavedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof ViewFinderSavedState)){
            super.onRestoreInstanceState(state);
            return;
        }
        ViewFinderSavedState viewFinderSavedState= (ViewFinderSavedState) state;
        super.onRestoreInstanceState(viewFinderSavedState.getSuperState());
        if(viewFinderSavedState.bitmap!=null)setImageBitmap(viewFinderSavedState.bitmap);
        setImageMatrix(viewFinderSavedState.matrix);
        initDedetector();
    }

    public static class ViewFinderSavedState extends BaseSavedState {
        Matrix matrix;
        Bitmap bitmap;

        public ViewFinderSavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            float[] values = new float[9];
            matrix.getValues(values);
            out.writeFloatArray(values);
            out.writeParcelable(bitmap,flags);
        }

        public ViewFinderSavedState(Parcel source) {
            super(source);
            float[] values = source.createFloatArray();
            matrix = new Matrix();
            matrix.setValues(values);
            bitmap=Bitmap.CREATOR.createFromParcel(source);
        }

        @SuppressWarnings("hiding")
        public static final Parcelable.Creator<ViewFinderSavedState> CREATOR = new Parcelable.Creator<ViewFinderSavedState>() {
            @Override
            public ViewFinderSavedState createFromParcel(Parcel source) {
                return new ViewFinderSavedState(source);
            }

            @Override
            public ViewFinderSavedState[] newArray(int size) {
                return new ViewFinderSavedState[size];
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int heigth= (int) (width/ASPECT_RATIO);
        setMeasuredDimension(width, heigth);
    }
}
