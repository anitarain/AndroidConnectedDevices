package com.example.android.wifidirect.discovery;


import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by anitaimani on 10/02/16.
 */


//still to use write object with ObjectOutputStream you have to implements Serializable
//All the fields are to be serializable. You can mark a field as transient if you want not to serialize it –
public class MessageType  implements  Serializable {


    private String WhatMessage;
    private String s1 = "";
    private String userRole;
    private String string;
    private PackageInfo packageName;
    private int integer;
    private transient Bitmap bitmap1;
    private Boolean aBoolean;
    private MotionEvent event;
    private String buttonClicked;
    private Update update;
    private String intent;

    public MessageType() {

    }


    public void setWhatMessage(String WhatMessage){
        this.WhatMessage = WhatMessage;
    }

    public String getWhatMessage(){
        return this.WhatMessage;
    }

    public Bitmap getBitmap() {
        return this.bitmap1;
    }

    public String getString() {
        return this.string;
    }

    public String getIntent() {
        return this.intent;
    }


    public int getInteger() {
        return this.integer;
    }

    public MotionEvent getMotionEvent() {
        return this.event;
    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap1 = bitmap;
    }

    public void setString(String string) {
        this.string = string;
    }
    public void setIntent(String intent) {
        this.intent = intent;
    }

    public void setInteger(int s5) {
        this.integer = s5;
    }

    public void setUpdate(Update update){
        this.update = update;
    }

    public Update getUpdate(){
        return this.update;
    }






//    public MessageType(String userRole) {
//        this.userRole = userRole;
//
//    }
//
//    public MessageType(MotionEvent event, String button) {
//        this.event = event;
//        buttonClicked = button;
//
//    }
//
//
//    public MessageType(Bitmap bitmap1, String s11) {
//        s3 = s11;
//        this.bitmap1 = bitmap1;
//
//
//    }






//        // Converts the Bitmap into a byte array for serialization
        private void writeObject (ObjectOutputStream out)throws IOException {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            out.writeObject(string);
            out.writeObject(WhatMessage);
            out.writeObject(userRole);
            out.writeObject(intent);
            out.writeInt(integer);
            out.writeObject(update);
            if(bitmap1 != null) {
                boolean success = bitmap1.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
                byte bitmapBytes[] = byteStream.toByteArray();
                if (success) {
                    out.write(bitmapBytes, 0, bitmapBytes.length);
                }

            }
        //out.writeObject(3);

    }

        // Deserializes a byte array representing the Bitmap and decodes it

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        string = (String) in.readObject();
        WhatMessage = (String) in.readObject();
        userRole = (String) in.readObject();
        intent = (String) in.readObject();
        integer = in.readInt();
        update = (Update) in.readObject();
            int b;
            while ((b = in.read()) != -1)
                byteStream.write(b);
            byte bitmapBytes[] = byteStream.toByteArray();
            bitmap1 = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);


    }


}









