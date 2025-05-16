package com.indian.ramiz.indianoilfetrcher;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {

    public static void RequestPermission(Activity activity, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    requestCode);
        } else {
            switchActivity(activity);
        }
    }

    public static void CheckPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            switchActivity(activity);
        } else {
            Toast.makeText(activity, "RECEIVE_SMS permission is not granted", Toast.LENGTH_SHORT).show();
        }
    }

    public static void handlePermission(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switchActivity(activity);
        } else {
            Toast.makeText(activity, "RECEIVE_SMS permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private static void switchActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeScreen.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
