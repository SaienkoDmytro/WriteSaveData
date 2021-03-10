package com.example.writesavedata;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

interface ResultHandler {
    void handleActionResult (@NonNull Context context,
                             int requestCode,
                             int resultCode,
                             @Nullable Intent data);
    }
