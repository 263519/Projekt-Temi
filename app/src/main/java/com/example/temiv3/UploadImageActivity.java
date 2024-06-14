package com.example.temiv3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadImageActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    private String location;
    private String description;
    private Bitmap capturedImage;
    private static final int REQUEST_IMAGE_PICK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        imageView = findViewById(R.id.imageView);
        Button takePhotoButton = findViewById(R.id.takePhotoButton);
        Button uploadButton = findViewById(R.id.uploadButton);
        Button exitButton = findViewById(R.id.exitButton);

        description = getIntent().getStringExtra("description");
        location = getIntent().getStringExtra("location");

        takePhotoButton.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        uploadButton.setOnClickListener(v -> {
            if (capturedImage != null) {
                uploadImage(location, description, capturedImage);
            } else {
                Toast.makeText(this, "Please take a photo first", Toast.LENGTH_SHORT).show();
            }
        });
        exitButton.setOnClickListener(v -> {
            finish(); // Zamyka bieżącą aktywność
        });
        Button choosePhotoButton = findViewById(R.id.choosePhotoButton);
        choosePhotoButton.setOnClickListener(v -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
        });
    }

    private void uploadImage(String location, String description, Bitmap image) {
        // Code to upload image
        ListItem listItem = new ListItem();
        listItem.updateImage(location, description, image, this);
        // Alternatively, you can call AsyncTask or any other method for image upload
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                capturedImage = (Bitmap) extras.get("data");
                imageView.setImageBitmap(capturedImage);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                try {
                    Uri selectedImage = data.getData();
                    capturedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    imageView.setImageBitmap(capturedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}