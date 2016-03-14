package com.example.android.wifidirect.discovery;

import android.app.Application;
import android.content.pm.PackageInfo;

/**
 * Created by anitaimani on 10/02/16.
 */
public class AppData extends Application {

    PackageInfo packageInfo;

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }
}