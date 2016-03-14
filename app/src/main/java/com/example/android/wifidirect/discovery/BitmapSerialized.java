package com.example.android.wifidirect.discovery;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by anitaimani on 21/02/16.
 */
public class BitmapSerialized implements Serializable {
    private static final long serialVersionUID = -5228835919664263905L;
    public transient Bitmap bitmap2;
    public String ss1;


    public BitmapSerialized() {

    }

    public BitmapSerialized(Bitmap b, String ss1) {
        bitmap2 = b;
        Log.d("bitmapSerialized", "Received" + bitmap2);
        this.ss1 = ss1;
    }


        // Converts the Bitmap into a byte array for serialization

    private void writeObject(ObjectOutputStream out) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        out.writeObject(ss1);
        boolean success = bitmap2.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
        byte bitmapBytes[] = byteStream.toByteArray();
        if (success)
            out.write(bitmapBytes, 0, bitmapBytes.length);
        //out.writeObject(3);

    }

    // Deserializes a byte array representing the Bitmap and decodes it
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ss1 = (String) in.readObject();
        int b;
        while ((b = in.read()) != -1)
            byteStream.write(b);
        byte bitmapBytes[] = byteStream.toByteArray();
        bitmap2 = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);

    }

    public Bitmap getBitmap() {
        return this.bitmap2;
    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap2 = bitmap;
    }

}

