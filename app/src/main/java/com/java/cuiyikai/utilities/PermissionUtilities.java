package com.java.cuiyikai.utilities;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class PermissionUtilities {

    private PermissionUtilities() {
        throw new IllegalStateException("Utility class");
    }

    public static int verifyPermissions(Activity activity, String permission) {
        int permissionCode = ContextCompat.checkSelfPermission(activity, permission);
        if(permissionCode == PackageManager.PERMISSION_GRANTED)
            return 1;
        else
            return 0;
    }
}
