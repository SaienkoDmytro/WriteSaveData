package com.example.writesavedata;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkingFile {

    private final static String FILE_NAME = "example.txt";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_CODE = 999;


    public static File createImageFile(final Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "jpg_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    public static void takePhoto(final Activity activity) {
        //if system os is >= marshmallow, request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                //permission not enabled, request it
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //show popup to request permissions
                activity.requestPermissions(permission, PERMISSION_CODE);
            }
            else {
                dispatchTakePictureIntent(activity);
            }
        }
        else {
            dispatchTakePictureIntent(activity);
        }
    }

    public static void dispatchTakePictureIntent(final Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(activity);
            } catch (IOException ex) {
                Toast toast = Toast.makeText(activity.getApplicationContext(),
                        R.string.cant_save_file, Toast.LENGTH_SHORT);
                toast.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,
                        "com.example.writesavedata.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public static void saveFile (String input, final Context context) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
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

    public static void shareFile(Activity activity) {
        File file = new File(activity.getFilesDir(), FILE_NAME);
        Uri fileUri = FileProvider.getUriForFile(activity, "com.example.writesavedata.provider", file);

        if (!file.exists()) {
            Toast.makeText(activity, activity.getText(R.string.error), Toast.LENGTH_SHORT).show();
        } else {
            Intent intentShare = new Intent(Intent.ACTION_SEND);
            intentShare.putExtra(Intent.EXTRA_STREAM, fileUri);
            intentShare.setType(activity.getString(R.string.type));
            intentShare.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            activity.startActivity(Intent.createChooser(intentShare, activity.getString(R.string.text_share)));
        }
    }
}
