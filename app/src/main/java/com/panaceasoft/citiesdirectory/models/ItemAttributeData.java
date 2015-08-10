package com.panaceasoft.citiesdirectory.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 19/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class ItemAttributeData implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("item_id")
    private int item_id;

    @SerializedName("shop_id")
    private int shop_id;

    @SerializedName("name")
    private String name;

    @SerializedName("detailString")
    private String detailString;

    @SerializedName("priceString")
    private String priceString;

    @SerializedName("details")
    private ArrayList<ItemAttributeDetailData> details;

    public int getId() {
        return id;
    }

    public int getItem_id() {
        return item_id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public String getName() {
        return name;
    }

    public String getDetailString() {
        return detailString;
    }

    public String getPriceString() {
        return priceString;
    }

    public ArrayList<ItemAttributeDetailData> getDetails() {
        return details;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetailString(String detailString) {
        this.detailString = detailString;
    }

    public void setPriceString(String priceString) {
        this.priceString = priceString;
    }

    public void setDetails(ArrayList<ItemAttributeDetailData> details) {
        this.details = details;
    }
}
