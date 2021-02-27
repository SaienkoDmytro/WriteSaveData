package com.example.writesavedata;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final static String FILE_NAME = "example.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            File file = new File(MainActivity.this.getFilesDir(), FILE_NAME);
            if (!file.exists()) {
                Toast.makeText(this, getText(R.string.error), Toast.LENGTH_SHORT).show();
            } else {
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType(getString(R.string.type));
                intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse(getString(R.string.file_name)));
                startActivity(Intent.createChooser(intentShare, getString(R.string.text_share)));
            }
        });
    }

    private void saveFile (String input) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fileOutputStream.write(input.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream !=null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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