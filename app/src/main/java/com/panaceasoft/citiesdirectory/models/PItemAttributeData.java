package com.panaceasoft.citiesdirectory.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 19/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class PItemAttributeData implements Parcelable {

    public int id;

    public int item_id;

    public int shop_id;

    public String name;

    public String detailString;

    public String priceString;

    public ArrayList<PItemAttributeDetailData> details;


    protected PItemAttributeData(Parcel in) {
        id = in.readInt();
        item_id = in.readInt();
        shop_id = in.readInt();
        name = in.readString();
        detailString = in.readString();
        priceString = in.readString();
        if (in.readByte() == 0x01) {
            details = new ArrayList<PItemAttributeDetailData>();
            in.readList(details, PItemAttributeDetailData.class.getClassLoader());
        } else {
            details = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(item_id);
        dest.writeInt(shop_id);
        dest.writeString(name);
        dest.writeString(detailString);
        dest.writeString(priceString);
        if (details == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(details);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PItemAttributeData> CREATOR = new Parcelable.Creator<PItemAttributeData>() {
        @Override
        public PItemAttributeData createFromParcel(Parcel in) {
            return new PItemAttributeData(in);
        }

        @Override
        public PItemAttributeData[] newArray(int size) {
            return new PItemAttributeData[size];
        }
    };
}