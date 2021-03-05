package com.example.writesavedata;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private final static String FILE_NAME = "example.txt";
    private static final int PERMISSION_CODE = 999;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imageView;
    private FloatingActionButton fab;
    private LinearLayout fabLayout1, fabLayout2, fabLayout3;
    private View fabBGLayout;
    private boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = findViewById(R.id.backgroundImageMainActivity);
        Glide.with(this).load(R.drawable.unnamed).into(imageView);

        fabLayout1 = findViewById(R.id.fabLayout1ShareFile);
        fabLayout2 =  findViewById(R.id.fabLayoutChosePhoto);
        fabLayout3 = findViewById(R.id.fabLayoutTakePhoto);
        fab = findViewById(R.id.fabNative);
        FloatingActionButton fab1 = findViewById(R.id.fabShareFile);
        FloatingActionButton fab2 = findViewById(R.id.fabChosePhoto);
        FloatingActionButton fab3 = findViewById(R.id.fabTakePhoto);
        fabBGLayout = findViewById(R.id.fabBGLayout);

        fab.setOnClickListener(view -> {
            if (!isFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        });

        fabBGLayout.setOnClickListener(view -> closeFABMenu());


        fab1.setOnClickListener(view -> {
            File file = new File(MainActivity.this.getFilesDir(), FILE_NAME);
            Uri fileUri = FileProvider.getUriForFile(MainActivity.this, "com.example.writesavedata.provider", file);

            if (!file.exists()) {
                Toast.makeText(this, getText(R.string.error), Toast.LENGTH_SHORT).show();
            } else {
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.putExtra(Intent.EXTRA_STREAM, fileUri);
                intentShare.setType(getString(R.string.type));
                intentShare.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivity(Intent.createChooser(intentShare, getString(R.string.text_share)));
            }
        });

        fab2.setOnClickListener(v -> {
            Intent chosePhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(chosePhoto, 1);

        });

        fab3.setOnClickListener(v -> {
            //if system os is >= marshmallow, request runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED){
                    //permission not enabled, request it
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //show popup to request permissions
                    requestPermissions(permission, PERMISSION_CODE);
                }
                else {
                    dispatchTakePictureIntent();
                }
            }
            else {
                dispatchTakePictureIntent();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            final Uri imageUri = data.getData();
            final InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Glide.with(this).load(selectedImage).into(imageView);
            } catch (FileNotFoundException e) {
            e.printStackTrace();
            }
        }
    }

    private void saveFile (String input) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fileOutputStream.write(input.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "jpg_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        String currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.cant_save_file, Toast.LENGTH_SHORT);
                toast.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.writesavedata.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);
        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotation(0);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout3.animate().translationY(0);
        fabLayout3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isFABOpen) {
            closeFABMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        saveFile(getString(R.string.onStart));
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveFile(getString(R.string.onStop));
    }

    @Override
    protected void onResume() {
        super.onResume();
        saveFile(getString(R.string.onResume));
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveFile(getString(R.string.onPause));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveFile(getString(R.string.onDestroy));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        saveFile(getString(R.string.onRestart));
    }
}