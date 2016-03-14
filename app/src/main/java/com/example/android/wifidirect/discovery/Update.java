package com.example.android.wifidirect.discovery;

import android.graphics.Matrix;

import java.io.Serializable;

/**
 * Created by anitaimani on 29/02/16.
 */
public class Update implements Serializable {
    private int u1;
    private String string;
    private Matrix matrix;
    //private Context context;
    //public Intent intent;



    public Update(){

    }

    public void setString (String string){
        this.string = string;
    }



    public String getString(){
        return this.string;
    }

    public void setInteger (int u1){
        this.u1 = u1;
    }

    public int getInteger(){
        return this.u1;
    }

//    public void setContext (Context context){
//        this.context = context;
//    }
//
//    public Context getContext(){
//        return this.context;
//    }

}
