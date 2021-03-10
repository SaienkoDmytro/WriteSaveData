package com.example.writesavedata;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class TakePhotoManager implements ResultHandler {

    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private static final int REQUEST_IMAGE_PICK = 103;
    private static final String FILE_NAME = "example.txt";
    private static final int PERMISSION_CODE = 101;
    private static String imageFilePath;
    private final ImageView mImageView;

    static void startAction(Activity activity) { takePhoto(activity); }

    public TakePhotoManager(ImageView imageView) { mImageView = imageView; }

    @Override
    public void handleActionResult(@NonNull Context context, int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Glide.with(context).load(imageFilePath).into(mImageView);
        }
        if (requestCode == REQUEST_IMAGE_PICK && data != null) {
            final Uri imageUri = data.getData();
            final InputStream imageStream;
            try {
                imageStream = context.getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Glide.with(context).load(selectedImage).into(mImageView);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static File createImageFile(final Activity activity) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
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

    public static void pickPhoto(final Activity activity) {
        Intent chosePhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(chosePhoto, REQUEST_IMAGE_PICK);
    }

    public static void saveFile(String input, final Activity activity) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = activity.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
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

    public static void shareFile(final Activity activity) {
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
