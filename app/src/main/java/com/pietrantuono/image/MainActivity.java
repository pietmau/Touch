package com.pietrantuono.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import hugo.weaving.DebugLog;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private final String TAG = getClass().getSimpleName();
    ImageView imageView;
    Bitmap bitmap;
    private int imageHeight;
    private int imageWidth;
    private Matrix displayMatrix;
    private MultiGestureDetector multiGestureDetector;
    private float previousAngle;

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
                if(imageView.getDrawable()!=null)multiGestureDetector.onTouchEvent(event);
                return true;
            }
        });
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
        }, imageView);

    }

    @DebugLog
    private void translateImage(float distanceX, float distanceY) {
        Matrix matrix = imageView.getImageMatrix();
        displayMatrix.set(matrix);
        displayMatrix.postTranslate(-distanceX, -distanceY);
        imageView.setImageMatrix(displayMatrix);
    }

    @DebugLog
    private void scaleImage(float scaleFactor, float focusX, float focusY) {
        Matrix matrix = imageView.getImageMatrix();
        Matrix displayMatrix = new Matrix();
        displayMatrix.set(matrix);
        displayMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
        imageView.setImageMatrix(displayMatrix);
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
                    }
                    imageView.setImageBitmap(bitmap);
                    imageWidth = imageView.getDrawable().getIntrinsicWidth();
                    imageHeight = imageView.getDrawable().getIntrinsicHeight();
                    RectF drawableRect = new RectF(0, 0, imageWidth, imageHeight);
                    RectF viewRect = new RectF(0, 0, imageView.getWidth(), imageView.getHeight());
                    Matrix matrix = new Matrix();
                    matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
                    displayMatrix = new Matrix();
                    displayMatrix.set(matrix);
                }
        }
    }


    private void saveImage() {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeigth();
        Matrix matrix = new Matrix();
        matrix.set(imageView.getImageMatrix());
        Bitmap notRotatedbitmap = Bitmap.createBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap(), 0, 0, ((BitmapDrawable) imageView.getDrawable()).getBitmap().getWidth(), ((BitmapDrawable) imageView.getDrawable()).getBitmap().getHeight(), matrix, true);
        Bitmap croppedbitmap = Bitmap.createBitmap(notRotatedbitmap, x, y, width, height);
        String root = Environment.getExternalStorageDirectory().toString();
        String fname = "Touchnote.jpg";
        File file = new File(root, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            croppedbitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getHeigth() {
        Matrix matrix = imageView.getImageMatrix();
        Matrix temp = new Matrix();
        temp.set(matrix);
        RectF rectFSource = new RectF();
        RectF rectFDestination = new RectF();
        rectFSource.top = 0;
        rectFSource.left = 0;
        rectFSource.right = imageView.getDrawable().getIntrinsicWidth();
        rectFSource.bottom = imageView.getDrawable().getIntrinsicHeight();
        Matrix imagemaxtrix = imageView.getImageMatrix();
        Matrix matrix1 = new Matrix();
        matrix1.set(imagemaxtrix);
        matrix1.mapRect(rectFDestination, rectFSource);

        RectF imageviewRetf = new RectF();
        imageviewRetf.top = 0;
        imageviewRetf.left = 0;
        imageviewRetf.right = imageView.getWidth();
        imageviewRetf.bottom = imageView.getHeight();

        RectF intersection = new RectF();
        intersection.set(imageviewRetf);

        if (intersection.intersect(rectFDestination)) {
            return (int) (intersection.bottom - intersection.top);
        } else return 0;
    }

    private int getWidth() {
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

        RectF imageviewRetf = new RectF();
        imageviewRetf.top = 0;
        imageviewRetf.left = 0;
        imageviewRetf.right = imageView.getWidth();
        imageviewRetf.bottom = imageView.getHeight();

        RectF intersection = new RectF();
        intersection.set(imageviewRetf);

        if (intersection.intersect(rectFDestination)) {
            return (int) (intersection.right - intersection.left);
        } else return 0;
    }

    private int getY() {
        RectF rectFSource = new RectF();
        RectF rectFDestination = new RectF();
        rectFSource.top = 0;
        rectFSource.left = 0;
        rectFSource.right = imageView.getDrawable().getIntrinsicWidth();
        rectFSource.bottom = imageView.getDrawable().getIntrinsicHeight();
        Matrix imagemaxtrix = imageView.getImageMatrix();
        Matrix matrix1 = new Matrix();
        matrix1.set(imagemaxtrix);
        matrix1.mapRect(rectFDestination, rectFSource);

        RectF imageviewRetf = new RectF();
        imageviewRetf.top = 0;
        imageviewRetf.left = 0;
        imageviewRetf.right = imageView.getWidth();
        imageviewRetf.bottom = imageView.getHeight();

        RectF modified = new RectF();
        modified.set(imageviewRetf);

        if (modified.intersect(rectFDestination)) {
            modified.offset(-rectFDestination.left, -rectFDestination.top);
            return (int) modified.top;
        } else return 0;

    }

    public int getX() {
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

        RectF imageviewRetf = new RectF();
        imageviewRetf.top = 0;
        imageviewRetf.left = 0;
        imageviewRetf.right = imageView.getWidth();
        imageviewRetf.bottom = imageView.getHeight();

        RectF modified = new RectF();
        modified.set(imageviewRetf);

        if (modified.intersect(rectFDestination)) {
            modified.offset(-rectFDestination.left, -rectFDestination.top);
            return (int) modified.left;
        } else return 0;
    }

    @DebugLog
    public void rotateImage(float currentDeltaAngle) {
        Matrix matrix = imageView.getImageMatrix();
        Matrix rotateMatrixx = new Matrix();
        rotateMatrixx.set(matrix);
        matrix = imageView.getImageMatrix();
        Matrix displayMatrix = new Matrix();
        displayMatrix.set(matrix);
        displayMatrix.postRotate(currentDeltaAngle, getImageViewCetnerX(), getImageViewCenterY());
        imageView.setImageMatrix(displayMatrix);
        previousAngle += currentDeltaAngle;
    }

    @DebugLog
    public void onEndRotation() {
        float angle = previousAngle;
        Log.d(TAG, "previousAngle = " + previousAngle);
        float snapAngle = 0;
        if (angle < 45 && angle >= 0) snapAngle = 0;
        else if (angle < 135 && angle >= 45) snapAngle = 90;
        else if (angle <= 180 && angle >= 135) snapAngle = 180;
        else if (angle <= 0 && angle > -45) snapAngle = 0;
        else if (angle <= -45 && angle > -135) snapAngle = -90;
        else if (angle <= -138 && angle >= -180) snapAngle = -180;
        Matrix matrix = imageView.getImageMatrix();
        Matrix rotateMatrixx = new Matrix();
        rotateMatrixx.set(matrix);
        rotateMatrixx.postRotate(-previousAngle, getImageViewCetnerX(), getImageViewCenterY());
        imageView.setImageMatrix(rotateMatrixx);
        matrix = imageView.getImageMatrix();
        Matrix displayMatrix = new Matrix();
        displayMatrix.set(matrix);
        displayMatrix.postRotate(snapAngle, getImageViewCetnerX(), getImageViewCenterY());
        imageView.setImageMatrix(displayMatrix);
        previousAngle = 0;
    }

    private float getImageViewCetnerX() {
        RectF imageviewRetf = new RectF();
        imageviewRetf.top = 0;
        imageviewRetf.left = 0;
        imageviewRetf.right = imageView.getWidth();
        imageviewRetf.bottom = imageView.getHeight();
        return imageviewRetf.centerX();
    }

    private float getImageViewCenterY() {
        RectF imageviewRetf = new RectF();
        imageviewRetf.top = 0;
        imageviewRetf.left = 0;
        imageviewRetf.right = imageView.getWidth();
        imageviewRetf.bottom = imageView.getHeight();
        return imageviewRetf.centerY();
    }
}
