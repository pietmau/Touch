package com.pietrantuono.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements RotationGestureDetector.OnRotationGestureListener {
    private static final int PICK_IMAGE = 1;
    private static final String TAG = "MainActivity";
    ImageView imageView;
    Bitmap bitmap;
    private ScaleGestureDetector mScaleDetector;//TODO  careful with api version
    private Matrix matrix = new Matrix();
    private int imageHeight;
    private int imageWidth;
    private Matrix displayMatrix;
    private float displayedImageHeigth;
    private float displayedImageWidth;
    private float translateX = 0;
    private float translateY = 0;
    private int offsetX = 0;
    private int offsetY = 0;
    private GestureDetectorCompat panDetector;
    private RotationGestureDetector rotationGestureDetector;
    private float startAngle;
    private float currentAngle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);

        findViewById(R.id.pick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick();
            }
        });
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                panDetector.onTouchEvent(event);
                rotationGestureDetector.onTouchEvent(event);
                return true;
            }
        });
        mScaleDetector = new ScaleGestureDetector(MainActivity.this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                scaleImage(scaleFactor, detector.getFocusX(), detector.getFocusY());
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                foo();
            }
        });

        panDetector = new GestureDetectorCompat(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                translateImage(distanceX, distanceY);
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return super.onDown(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }
        });
        rotationGestureDetector = new RotationGestureDetector(MainActivity.this);

    }

    private void translateImage(float distanceX, float distanceY) {
        matrix = imageView.getImageMatrix();
        displayMatrix.set(matrix);
        displayMatrix.postTranslate(-distanceX, -distanceY);

        imageView.setImageMatrix(displayMatrix);

        foo();

    }

    private void scaleImage(float scaleFactor, float focusX, float focusY) {
        matrix = imageView.getImageMatrix();
        displayMatrix.set(matrix);
        displayMatrix.postTranslate(-translateX, -translateY);

        displayMatrix.postScale(scaleFactor, scaleFactor);

        float[] f = new float[9];
        displayMatrix.getValues(f);

        float scaleX = f[Matrix.MSCALE_X];
        float scaleY = f[Matrix.MSCALE_Y];

        displayedImageWidth = imageWidth * scaleX;
        Log.d(TAG, "displayedImageWidth " + displayedImageWidth);
        displayedImageHeigth = imageHeight * scaleY;
        translateX = (imageView.getWidth() - displayedImageWidth) / 2;
        Log.d(TAG, "translateX " + translateX);
        translateY = (imageView.getHeight() - displayedImageHeigth) / 2;

        displayMatrix.postTranslate(translateX, translateY);


        imageView.setImageMatrix(displayMatrix);

        foo();

    }

    private void save() {

    }

    private void pick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //TODO deal withn large bitmaps!
                    foo();
                    imageView.setImageBitmap(bitmap);
                    imageWidth = imageView.getDrawable().getIntrinsicWidth();
                    imageHeight = imageView.getDrawable().getIntrinsicHeight();
                    RectF drawableRect = new RectF(0, 0, imageWidth, imageHeight);
                    RectF viewRect = new RectF(0, 0, imageView.getWidth(), imageView.getHeight());

                    matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
                    //imageView.setImageMatrix(matrix);
                    displayMatrix = new Matrix();
                    displayMatrix.set(matrix);
                    float[] f = new float[9];
                    imageView.getImageMatrix().getValues(f);

                    float scaleX = f[Matrix.MSCALE_X];
                    float scaleY = f[Matrix.MSCALE_Y];

                    displayedImageWidth = imageWidth * scaleX;
                    displayedImageHeigth = imageHeight * scaleY;
                    translateX = (imageView.getWidth() - displayedImageWidth) / 2;
                    translateY = (imageView.getHeight() - displayedImageHeigth) / 2;

                }
        }
    }

    private void foo() {

    }

    private void saveImage() {
        Bitmap bimap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeigth();
        Log.d(TAG,"Saving, angle = "+getCurrentAngle());


        Matrix matrix= new Matrix();
        matrix.postRotate(-getCurrentAngle(),imageView.getWidth()/2,imageView.getHeight()/2);
        //Bitmap notRotatedbitmap= Bitmap.createBitmap(bimap, 0, 0, bimap.getWidth(), bimap.getHeight(), matrix, true);
        float angle = getCurrentAngle();
        Log.d(TAG, "onsve angel "+angle);
        //Bitmap scaledBitmap = Bitmap.createBitmap(bimap, x, y, width, height);
        Bitmap notRotatedbitmap= Bitmap.createBitmap(bimap, 0, 0, bimap.getWidth(), bimap.getHeight());
        Bitmap scaledBitmap = Bitmap.createBitmap(notRotatedbitmap, x, y, width, height);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(),matrix,true);
        String root = Environment.getExternalStorageDirectory().toString();
        String fname = "Touchnote.jpg";
        File file = new File(root, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //scaledBitmap.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "image/*");
        startActivity(intent);
    }

    private int getHeigth() {
        Matrix matrix = imageView.getImageMatrix();
        Matrix temp = new Matrix();
        temp.set(matrix);
        float[] f = new float[9];
        temp.getValues(f);
        float scaleX = f[Matrix.MSCALE_X];
        float scaleY = f[Matrix.MSCALE_Y];
        float shewx = f[Matrix.MSKEW_X];
        float shewy = f[Matrix.MSKEW_Y];
        float trsx = f[Matrix.MTRANS_X];
        float trY = f[Matrix.MTRANS_Y];

        RectF rectFSource= new RectF();
        RectF rectFDestination= new RectF();
        rectFSource.top=0;
        rectFSource.left=0;
        rectFSource.right=imageView.getDrawable().getIntrinsicWidth();
        rectFSource.bottom=imageView.getDrawable().getIntrinsicHeight();
        Matrix imagemaxtrix = imageView.getImageMatrix();//;
        Matrix matrix1=new Matrix();
        matrix1.set(imagemaxtrix);
        matrix1.mapRect(rectFDestination, rectFSource);

        float checkpositive = (imageView.getHeight()-rectFDestination.top )/ ((-rectFDestination.top + rectFDestination.bottom) / rectFSource.bottom);
        float checknegative = ((-rectFDestination.top + rectFDestination.bottom)+rectFDestination.top )/ ((-rectFDestination.top + rectFDestination.bottom) / rectFSource.bottom);
        float originalnegative = ((imageView.getDrawable().getIntrinsicHeight()) + trY / scaleY);
        float originalpositive = (imageView.getHeight() / scaleY - trY / scaleY);

        if(rectFDestination.top>0)return (int) checkpositive;
        else return (int) checknegative;
        //return (int) checkpositive;
        //if (rectFDestination.top > 0) return (int) (imageView.getHeight() / scaleY - trY / scaleY);
        //else return (int) ((imageView.getDrawable().getIntrinsicHeight()) + trY / scaleY);
    }

    private int getWidth() {
        Matrix matrix = imageView.getImageMatrix();
        Matrix temp = new Matrix();
        temp.set(matrix);
        float[] f = new float[9];
        temp.getValues(f);
        float scaleX = f[Matrix.MSCALE_X];
        float scaleY = f[Matrix.MSCALE_Y];
        float shewx = f[Matrix.MSKEW_X];
        float shewy = f[Matrix.MSKEW_Y];
        float trsx = f[Matrix.MTRANS_X];
        float trY = f[Matrix.MTRANS_Y];
        temp.postRotate(-getCurrentAngle(),imageView.getWidth()/2,imageView.getHeight()/2);
        float angle = getCurrentAngle();
        f = new float[9];
        temp.getValues(f);
        scaleX = f[Matrix.MSCALE_X];
        scaleY = f[Matrix.MSCALE_Y];
        shewx = f[Matrix.MSKEW_X];
        shewy = f[Matrix.MSKEW_Y];
        trsx = f[Matrix.MTRANS_X];
        trY = f[Matrix.MTRANS_Y];

        RectF rectFSource= new RectF();
        RectF rectFDestination= new RectF();
        rectFSource.top=0;
        rectFSource.left=0;
        rectFSource.right = imageView.getDrawable().getIntrinsicWidth();
        rectFSource.bottom=imageView.getDrawable().getIntrinsicHeight();

        Matrix imagemaxtrix = imageView.getImageMatrix();//;
        Matrix matrix1=new Matrix();
        matrix1.set(imagemaxtrix);
        matrix1.mapRect(rectFDestination, rectFSource);

        float checkpositive =((imageView.getWidth()-rectFDestination.left) )/ ((-rectFDestination.left + rectFDestination.right) / rectFSource.right);
        float checknegative = rectFDestination.right / ((-rectFDestination.left + rectFDestination.right) / rectFSource.right);
        float originalnegative = (imageView.getDrawable().getIntrinsicWidth() + trsx / scaleX);
        float originalpositive = (imageView.getWidth() / scaleX - trsx / scaleX);

        if(rectFDestination.left>0)return (int) checkpositive;
        else return (int) checknegative;
        //return (int) checknegative;
        //if (rectFDestination.left > 0) return (int) (imageView.getWidth() / scaleX - trsx / scaleX);
        //else return (int) (imageView.getDrawable().getIntrinsicWidth() + trsx / scaleX);
    }

    private int getY() {
        Matrix matrix = imageView.getImageMatrix();
        Matrix temp = new Matrix();
        temp.set(matrix);
        float[] f = new float[9];
        temp.getValues(f);
        float scaleX = f[Matrix.MSCALE_X];
        float scaleY = f[Matrix.MSCALE_Y];
        float shewx = f[Matrix.MSKEW_X];
        float shewy = f[Matrix.MSKEW_Y];
        float trsx = f[Matrix.MTRANS_X];
        float trY = f[Matrix.MTRANS_Y];

        RectF rectFSource= new RectF();
        RectF rectFDestination= new RectF();
        rectFSource.top=0;
        rectFSource.left=0;
        rectFSource.right = imageView.getDrawable().getIntrinsicWidth();
        rectFSource.bottom=imageView.getDrawable().getIntrinsicHeight();

        Matrix imagemaxtrix = imageView.getImageMatrix();//;
        Matrix matrix1=new Matrix();
        matrix1.set(imagemaxtrix);
        matrix1.mapRect(rectFDestination, rectFSource);

        float check = (rectFDestination.top) / ((-rectFDestination.bottom + rectFDestination.top) / rectFSource.bottom);
        float original = (-trY / scaleY);

        if (rectFDestination.top > 0) return 0;
        //else return (int) (-trY / scaleY);
        else return (int) check;
    }

    public int getX() {
        Matrix matrix = imageView.getImageMatrix();
        Matrix temp = new Matrix();
        temp.set(matrix);
        float[] f = new float[9];
        temp.getValues(f);
        float scaleX = f[Matrix.MSCALE_X];
        float scaleY = f[Matrix.MSCALE_Y];
        float shewx = f[Matrix.MSKEW_X];
        float shewy = f[Matrix.MSKEW_Y];
        float trsx = f[Matrix.MTRANS_X];
        float trY = f[Matrix.MTRANS_Y];

        RectF rectFSource= new RectF();
        RectF rectFDestination= new RectF();
        rectFSource.top=0;
        rectFSource.left=0;
        rectFSource.right = imageView.getDrawable().getIntrinsicWidth();
        rectFSource.bottom=imageView.getDrawable().getIntrinsicHeight();

        Matrix imagemaxtrix = imageView.getImageMatrix();//;
        Matrix matrix1=new Matrix();
        matrix1.set(imagemaxtrix);
        matrix1.mapRect(rectFDestination, rectFSource);

        float check = (-rectFDestination.left) / ((-rectFDestination.left + rectFDestination.right) / rectFSource.right);
        float original = (-trsx / scaleX);
        if (rectFDestination.left > 0) return 0;
        //else return (int) (-trsx / scaleX);
        else return (int) check;
    }

    public float getScaleX() {
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);
        float scaleX = f[Matrix.MSCALE_X];
        return scaleX;
    }

    public float getScaleY() {
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);
        float scaleY = f[Matrix.MSCALE_Y];
        return scaleY;
    }

    @Override
    public void onRotation(RotationGestureDetector rotationDetector) {
        Log.d(TAG, "onRotation " + rotationDetector.getAngle());
        Matrix matrix = imageView.getImageMatrix();
        Matrix rotateMatrixx = new Matrix();
        rotateMatrixx.set(matrix);
        rotateMatrixx.postRotate(getCurrentAngle(), imageView.getWidth() / 2, imageView.getHeight() / 2);
        Log.d(TAG, "onRotation currentAngle " + getCurrentAngle());
        imageView.setImageMatrix(rotateMatrixx);

        matrix = imageView.getImageMatrix();
        Matrix displayMatrix = new Matrix();
        displayMatrix.set(matrix);
        float rotationAngle = rotationDetector.getAngle() + startAngle;
        displayMatrix.postRotate(-rotationAngle, imageView.getWidth() / 2, imageView.getHeight() / 2);
        Log.d(TAG, "onRotation rotationAngle = " + rotationAngle);

        imageView.setImageMatrix(displayMatrix);
        getCurrentAngle();
    }

    @Override
    public void onEndRotation() {
        Log.d(TAG,"onEndRotation "+getCurrentAngle());
        float angle = getCurrentAngle();
        float snapAngle=0;
        if(angle<45 && angle>=0) snapAngle=0;
        else if(angle<135 && angle>=45)snapAngle=90;
        else if(angle<=180 && angle>=135)snapAngle=180;
        else if(angle<=0 && angle>-45)snapAngle=0;
        else if(angle<=-45 && angle>-135)snapAngle=-90;
        else if(angle<=-138 && angle>=-180)snapAngle=-180;
        Log.d(TAG,"Snap ="+snapAngle);
        Matrix matrix = imageView.getImageMatrix();
        Matrix rotateMatrixx = new Matrix();
        rotateMatrixx.set(matrix);
        rotateMatrixx.postRotate(getCurrentAngle(), imageView.getWidth() / 2, imageView.getHeight() / 2);
        imageView.setImageMatrix(rotateMatrixx);

        matrix = imageView.getImageMatrix();
        Matrix displayMatrix = new Matrix();
        displayMatrix.set(matrix);
        displayMatrix.postRotate(-snapAngle, imageView.getWidth() / 2, imageView.getHeight() / 2);

        imageView.setImageMatrix(displayMatrix);
    }

    @Override
    public void onStartRotation() {
        startAngle = getCurrentAngle();
        Log.d(TAG,"onStartRotation "+startAngle);
        currentAngle=0;
    }

    public float getCurrentAngle() {
        Matrix matrix = imageView.getImageMatrix();
        Matrix temp = new Matrix();
        temp.set(matrix);
        float[] f = new float[9];
        temp.getValues(f);
        float schewX = f[Matrix.MSKEW_Y];
        float schewY = f[Matrix.MSKEW_X];
        float m1 = f[Matrix.MPERSP_1];
        float m0 = f[Matrix.MPERSP_0];
        float m2 = f[Matrix.MPERSP_2];
        float trasx = f[Matrix.MTRANS_X];
        float trasy = f[Matrix.MTRANS_Y];
        float scaleX = f[Matrix.MSCALE_X];
        float scaleY = f[Matrix.MSCALE_Y];

        float rAngle = (float) (Math.atan2(f[Matrix.MSKEW_X], f[Matrix.MSCALE_X]) * (180 / Math.PI));
        Log.d(TAG, "Caluclated angle " + rAngle);
        return rAngle;


    }
}
