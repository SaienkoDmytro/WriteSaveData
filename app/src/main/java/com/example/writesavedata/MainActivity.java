package com.example.writesavedata;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private LinearLayout fabLayout1, fabLayout2, fabLayout3;
    private View fabBGLayout;
    private boolean isFABOpen = false;
    private List<ResultHandler> resultHandlers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imageView = findViewById(R.id.backgroundImageMainActivity);
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

        fab1.setOnClickListener(view -> TakePhotoManager.shareFile(this));

        fab2.setOnClickListener(v -> TakePhotoManager.pickPhoto(this));

        fab3.setOnClickListener(v -> TakePhotoManager.startAction(this));

        ArrayList<ResultHandler> handlers = new ArrayList<>();
        handlers.add(new TakePhotoManager(imageView));
        resultHandlers = handlers;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       for (ResultHandler resultHandler : resultHandlers) {
           resultHandler.handleActionResult(this, requestCode, resultCode, data);
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
        TakePhotoManager.saveFile(getString(R.string.onStart), this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TakePhotoManager.saveFile(getString(R.string.onStop), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TakePhotoManager.saveFile(getString(R.string.onResume), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TakePhotoManager.saveFile(getString(R.string.onPause), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TakePhotoManager.saveFile(getString(R.string.onDestroy), this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        TakePhotoManager.saveFile(getString(R.string.onRestart), this);
    }
}