package com.panaceasoft.citiesdirectory.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Panacea-Soft on 19/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class PItemAttributeDetailData implements Parcelable {

    public int id;

    public int shop_id;

    public int header_id;

    public int item_id;

    public String name;

    public float additional_price;

    public String added;


    protected PItemAttributeDetailData(Parcel in) {

        id = in.readInt();
        shop_id = in.readInt();
        header_id = in.readInt();
        item_id = in.readInt();
        name = in.readString();
        additional_price = in.readFloat();
        added = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(shop_id);
        dest.writeInt(header_id);
        dest.writeInt(item_id);
        dest.writeString(name);
        dest.writeFloat(additional_price);
        dest.writeString(added);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PItemAttributeDetailData> CREATOR = new Parcelable.Creator<PItemAttributeDetailData>() {
        @Override
        public PItemAttributeDetailData createFromParcel(Parcel in) {
            return new PItemAttributeDetailData(in);
        }

        @Override
        public PItemAttributeDetailData[] newArray(int size) {
            return new PItemAttributeDetailData[size];
        }
    };
}
