package com.example.writesavedata;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.writesavedata.WorkingFile.*;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

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

        fab1.setOnClickListener(view -> shareFile(this));

        fab2.setOnClickListener(v -> {
            Intent chosePhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(chosePhoto, REQUEST_IMAGE_CAPTURE);

        });

        fab3.setOnClickListener(v -> takePhoto(this));

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
        saveFile(getString(R.string.onStart), this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveFile(getString(R.string.onStop), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        saveFile(getString(R.string.onResume), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveFile(getString(R.string.onPause), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveFile(getString(R.string.onDestroy), this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        saveFile(getString(R.string.onRestart), this);
    }
}