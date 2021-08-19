package com.java.cuiyikai.utilities;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class PermissionUtilities {
    public static int verifyPermissions(Activity activity, String permission) {
        int permissionCode = ActivityCompat.checkSelfPermission(activity, permission);
        if(permissionCode == PackageManager.PERMISSION_GRANTED)
            return 1;
        else
            return 0;
    }
}
