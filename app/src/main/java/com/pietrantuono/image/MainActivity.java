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
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import hugo.weaving.DebugLog;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private final String TAG = getClass().getSimpleName();
    ViewFinder viewFinder;
    private int imageHeight;
    private int imageWidth;
    private MultiGestureDetector multiGestureDetector;
    private float previousAngle;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewFinder = (ViewFinder) findViewById(R.id.image);
        viewFinder.setScaleType(ImageView.ScaleType.MATRIX);
        findViewById(R.id.pick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick();
            }
        });
        findViewById(R.id.save).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewFinder.saveImage();
                    }
                });
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
                        viewFinder.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage));
                        this.selectedImage=selectedImage;
                    } catch (Exception e) {
                    }
                }
        }
    }


}
