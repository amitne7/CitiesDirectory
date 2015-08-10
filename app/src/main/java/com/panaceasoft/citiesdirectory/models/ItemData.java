package com.panaceasoft.citiesdirectory.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 18/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class ItemData implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("cat_id")
    private int cat_id;

    @SerializedName("sub_cat_id")
    private int sub_cat_id;

    @SerializedName("shop_id")
    private int shop_id;

    @SerializedName("discount_type_id")
    private int discount_type_id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("unit_price")
    private float unit_price;

    @SerializedName("search_tag")
    private String search_tag;

    @SerializedName("is_publishied")
    private int is_publishied;

    @SerializedName("added")
    private String added;

    @SerializedName("updated")
    private String updated;

    @SerializedName("like_count")
    private String like_count;

    @SerializedName("review_count")
    private String review_count;

    @SerializedName("inquiries_count")
    private int inquiries_count;

    @SerializedName("touches_count")
    private int touches_count;

    @SerializedName("discount_name")
    private String discount_name;

    @SerializedName("discount_persent")
    private String discount_persent;

    @SerializedName("reviews")
    private ArrayList<ReviewData> reviewData;

    @SerializedName("images")
    private ArrayList<ImageData> imageData;

    @SerializedName("attributes")
    private ArrayList<ItemAttributeData> itemAttributeData;

    public int getId() {
        return id;
    }

    public int getCat_id() {
        return cat_id;
    }

    public int getSub_cat_id() {
        return sub_cat_id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public int getDiscount_type_id() {
        return discount_type_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getUnit_price() {
        return unit_price;
    }

    public String getSearch_tag() {
        return search_tag;
    }

    public int getIs_publishied() {
        return is_publishied;
    }

    public String getAdded() {
        return added;
    }

    public String getUpdated() {
        return updated;
    }

    public String getLike_count() {
        return like_count;
    }

    public String getReview_count() {
        return review_count;
    }

    public int getInquiries_count() {
        return inquiries_count;
    }

    public int getTouches_count() {
        return touches_count;
    }

    public String getDiscount_name() {
        return discount_name;
    }

    public String getDiscount_persent() {
        return discount_persent;
    }

    public ArrayList<ReviewData> getReviewData() {
        return reviewData;
    }

    public ArrayList<ImageData> getImageData() {
        return imageData;
    }

    public ArrayList<ItemAttributeData> getItemAttributeData() {
        return itemAttributeData;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public void setSub_cat_id(int sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public void setDiscount_type_id(int discount_type_id) {
        this.discount_type_id = discount_type_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUnit_price(float unit_price) {
        this.unit_price = unit_price;
    }

    public void setSearch_tag(String search_tag) {
        this.search_tag = search_tag;
    }

    public void setIs_publishied(int is_publishied) {
        this.is_publishied = is_publishied;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public void setReview_count(String review_count) {
        this.review_count = review_count;
    }

    public void setInquiries_count(int inquiries_count) {
        this.inquiries_count = inquiries_count;
    }

    public void setTouches_count(int touches_count) {
        this.touches_count = touches_count;
    }

    public void setDiscount_name(String discount_name) {
        this.discount_name = discount_name;
    }

    public void setDiscount_persent(String discount_persent) {
        this.discount_persent = discount_persent;
    }

    public void setReviewData(ArrayList<ReviewData> reviewData) {
        this.reviewData = reviewData;
    }

    public void setImageData(ArrayList<ImageData> imageData) {
        this.imageData = imageData;
    }

    public void setItemAttributeData(ArrayList<ItemAttributeData> itemAttributeData) {
        this.itemAttributeData = itemAttributeData;
    }
}
