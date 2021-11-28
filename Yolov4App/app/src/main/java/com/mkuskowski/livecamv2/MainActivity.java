package com.mkuskowski.livecamv2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    final static int REQUEST_CODE_PHOTO_CAMERA = 2021;
    final static int REQUEST_CODE_PHOTO_GALLERY = 2020;


    ImageView imageView;
    Button btnPhoto;
    Button btnGallery;
    Button btnDetect;
    Button btnCamera;
    Bitmap photo;
    CustomDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        btnPhoto = findViewById(R.id.btnPhoto);
        btnDetect = findViewById(R.id.btnDetect);
        btnGallery = findViewById(R.id.btnGallery);
        btnCamera = findViewById(R.id.btnCamera);

        btnGallery.setOnClickListener( v -> GetImageFromGallery() );
        btnPhoto.setOnClickListener( v -> TakeAPhoto() );
        btnDetect.setOnClickListener( v -> Detect() );
        btnCamera.setOnClickListener( v->DetectFromCamera() );

        try
        {
            detector = new CustomDetector(this.getAssets());
        }
        catch (IOException e)
        {
            Log.println(Log.ERROR, "MainActivity.onCreate", "customDetector: " + e.getMessage());
        }

    }

    public void TakeAPhoto()
    {
        Intent newCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(newCamera, REQUEST_CODE_PHOTO_CAMERA);
    }
    public void GetImageFromGallery()
    {
        // Otwiera galerie i można stamtąd wybrać zdjęcie.
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra("return-data", true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_PHOTO_GALLERY);
    }

    public void Detect()
    {
        if(photo != null)
        {
            Log.println(Log.INFO, "@DEBUGAPPLOG", "We have photo");
            Bitmap cropBitMap = CustomDetector.processBitmap(photo, CustomDetector.INPUT_SIZE);
            detector.Detect(cropBitMap, imageView, true);
        }
        else
        {
            Log.println(Log.ERROR, "@DEBUGAPPLOG", "No photo");
        }
    }

    public void DetectFromCamera()
    {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null)
        {
            switch (requestCode)
            {
                case MainActivity.REQUEST_CODE_PHOTO_CAMERA:
                {
                    Bundle bundle = data.getExtras();
                    Bitmap photo = (Bitmap) bundle.get("data");
                    this.photo = photo;
                    imageView.setImageBitmap(photo);
                    break;
                }
                case MainActivity.REQUEST_CODE_PHOTO_GALLERY:
                {
                    Uri selectedImage = data.getData();
                    try
                    {
                        Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        this.photo = photo;
                        imageView.setImageBitmap(photo);
                    }
                    catch (IOException e)
                    {
                        Log.i("@DEMOAPPLOG", "Error:  " + e);
                    }
                    break;
                }
            }
        }
    }



}