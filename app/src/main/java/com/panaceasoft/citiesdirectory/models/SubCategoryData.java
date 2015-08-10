package com.panaceasoft.citiesdirectory.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Panacea-Soft on 15/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class SubCategoryData implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("cat_id")
    private int cat_id;

    @SerializedName("shop_id")
    private int shop_id;

    @SerializedName("name")
    private String name;

    @SerializedName("is_published")
    private int is_published;

    @SerializedName("ordering")
    private int ordering;

    @SerializedName("added")
    private String added;

    @SerializedName("updated")
    private String updated;

    @SerializedName("cover_image_file")
    private String cover_image_file;

    @SerializedName("cover_image_width")
    private int cover_image_width;

    @SerializedName("cover_image_height")
    private int cover_image_height;

    public int getId() {
        return id;
    }

    public int getCat_id() {
        return cat_id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public String getName() {
        return name;
    }

    public int getIs_published() {
        return is_published;
    }

    public int getOrdering() {
        return ordering;
    }

    public String getAdded() {
        return added;
    }

    public String getUpdated() {
        return updated;
    }

    public String getCover_image_file() {
        return cover_image_file;
    }

    public int getCover_image_width() {
        return cover_image_width;
    }

    public int getCover_image_height() {
        return cover_image_height;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIs_published(int is_published) {
        this.is_published = is_published;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public void setCover_image_file(String cover_image_file) {
        this.cover_image_file = cover_image_file;
    }

    public void setCover_image_width(int cover_image_width) {
        this.cover_image_width = cover_image_width;
    }

    public void setCover_image_height(int cover_image_height) {
        this.cover_image_height = cover_image_height;
    }

}
