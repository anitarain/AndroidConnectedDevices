package com.example.android.wifidirect.discovery;

/**
 * Created by anitaimani on 15/02/16.
 */
import android.os.Parcel;
import android.os.Parcelable;

public class Parcels {

    public static void writeBoolean(Parcel dest, Boolean value) {
        dest.writeByte((byte) (value != null && value ? 1 : 0));
    }

    public static Boolean readBoolean(Parcel in){
        return in.readByte() != 0;
    }

    public static void writeParcelable(Parcel dest, int flags, Parcelable parcelable) {
        writeBoolean(dest, parcelable == null);
        if (parcelable != null) {
            parcelable.writeToParcel(dest, flags);
        }
    }

    public static <T extends Parcelable> T readParcelable(Parcel in, Parcelable.Creator<T> creator) {
        boolean isNull = readBoolean(in);
        return isNull ? null : creator.createFromParcel(in);
    }

}