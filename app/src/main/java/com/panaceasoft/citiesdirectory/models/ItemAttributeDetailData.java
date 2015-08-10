package com.panaceasoft.citiesdirectory.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Panacea-Soft on 19/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class ItemAttributeDetailData implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("shop_id")
    private int shop_id;

    @SerializedName("header_id")
    private int header_id;

    @SerializedName("item_id")
    private int item_id;

    @SerializedName("name")
    private String name;

    @SerializedName("additional_price")
    private float additional_price;

    @SerializedName("added")
    private String added;

    public int getId() {
        return id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public int getHeader_id() {
        return header_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public String getName() {
        return name;
    }

    public float getAdditional_price() {
        return additional_price;
    }

    public String getAdded() {
        return added;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public void setHeader_id(int header_id) {
        this.header_id = header_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdditional_price(float additional_price) {
        this.additional_price = additional_price;
    }

    public void setAdded(String added) {
        this.added = added;
    }
}
