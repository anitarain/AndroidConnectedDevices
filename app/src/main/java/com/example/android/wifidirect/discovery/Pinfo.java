package com.example.android.wifidirect.discovery;

import android.graphics.drawable.Drawable;

/**
 * Created by anitaimani on 09/02/16.
 */
public class Pinfo {

    private String packageName;
    private String name;
    private Drawable icon;
    private boolean checked;

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;

    }
}




