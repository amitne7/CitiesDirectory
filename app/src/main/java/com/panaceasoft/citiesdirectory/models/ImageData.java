package com.panaceasoft.citiesdirectory.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Panacea-Soft on 18/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class ImageData implements Serializable{

    @SerializedName("id")
    private int id;

    @SerializedName("parent_id")
    private int parent_id;

    @SerializedName("shop_id")
    private int shop_id;

    @SerializedName("type")
    private String type;

    @SerializedName("path")
    private String path;

    @SerializedName("width")
    private float width;

    @SerializedName("height")
    private float height;

    @SerializedName("description")
    private String description;

    public int getId() {
        return id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
