package com.panaceasoft.citiesdirectory.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 15/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class CityData implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("address")
    private String address;

    @SerializedName("lat")
    private String lat;

    @SerializedName("lng")
    private String lng;

    @SerializedName("paypal_email")
    private String paypal_email;

    @SerializedName("paypal_payment_type")
    private String paypal_payment_type;

    @SerializedName("paypal_environment")
    private String paypal_environment;

    @SerializedName("paypal_appid_live")
    private String paypal_appid_live;

    @SerializedName("paypal_merchantname")
    private String paypal_merchantname;

    @SerializedName("paypal_customerid")
    private String paypal_customerid;

    @SerializedName("paypal_ipnurl")
    private String paypal_ipnurl;

    @SerializedName("paypal_memo")
    private String paypal_memo;

    @SerializedName("bank_account")
    private String bank_account;

    @SerializedName("bank_name")
    private String bank_name;

    @SerializedName("bank_code")
    private String bank_code;

    @SerializedName("swift_code")
    private String swift_code;

    @SerializedName("cod_email")
    private String cod_email;

    @SerializedName("currency_symbol")
    private String currency_symbol;

    @SerializedName("currency_short_form")
    private String currency_short_form;

    @SerializedName("sender_email")
    private String sender_email;

    @SerializedName("flat_rate_shipping")
    private String flat_rate_shipping;

    @SerializedName("keyword")
    private String keyword;

    @SerializedName("added")
    private String added;

    @SerializedName("status")
    private int status;

    @SerializedName("item_count")
    private int item_count;

    @SerializedName("category_count")
    private int category_count;

    @SerializedName("follow_count")
    private int follow_count;

    @SerializedName("cover_image_file")
    private String cover_image_file;

    @SerializedName("cover_image_width")
    private int cover_image_width;

    @SerializedName("cover_image_height")
    private int cover_image_height;

    @SerializedName("cover_image_description")
    private String cover_image_description;

    @SerializedName("categories")
    private ArrayList<CategoryData> categoryData;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getPaypal_email() {
        return paypal_email;
    }

    public String getPaypal_payment_type() {
        return paypal_payment_type;
    }

    public String getPaypal_environment() {
        return paypal_environment;
    }

    public String getPaypal_appid_live() {
        return paypal_appid_live;
    }

    public String getPaypal_merchantname() {
        return paypal_merchantname;
    }

    public String getPaypal_customerid() {
        return paypal_customerid;
    }

    public String getPaypal_ipnurl() {
        return paypal_ipnurl;
    }

    public String getPaypal_memo() {
        return paypal_memo;
    }

    public String getBank_account() {
        return bank_account;
    }

    public String getBank_name() {
        return bank_name;
    }

    public String getBank_code() {
        return bank_code;
    }

    public String getSwift_code() {
        return swift_code;
    }

    public String getCod_email() {
        return cod_email;
    }

    public String getCurrency_symbol() {
        return currency_symbol;
    }

    public String getCurrency_short_form() {
        return currency_short_form;
    }

    public String getSender_email() {
        return sender_email;
    }

    public String getFlat_rate_shipping() {
        return flat_rate_shipping;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getAdded() {
        return added;
    }

    public int getStatus() {
        return status;
    }

    public int getItem_count() {
        return item_count;
    }

    public int getCategory_count() {
        return category_count;
    }

    public int getFollow_count() {
        return follow_count;
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

    public String getCover_image_description() {
        return cover_image_description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setPaypal_email(String paypal_email) {
        this.paypal_email = paypal_email;
    }

    public void setPaypal_payment_type(String paypal_payment_type) {
        this.paypal_payment_type = paypal_payment_type;
    }

    public void setPaypal_environment(String paypal_environment) {
        this.paypal_environment = paypal_environment;
    }

    public void setPaypal_appid_live(String paypal_appid_live) {
        this.paypal_appid_live = paypal_appid_live;
    }

    public void setPaypal_merchantname(String paypal_merchantname) {
        this.paypal_merchantname = paypal_merchantname;
    }

    public void setPaypal_customerid(String paypal_customerid) {
        this.paypal_customerid = paypal_customerid;
    }

    public void setPaypal_ipnurl(String paypal_ipnurl) {
        this.paypal_ipnurl = paypal_ipnurl;
    }

    public void setPaypal_memo(String paypal_memo) {
        this.paypal_memo = paypal_memo;
    }

    public void setBank_account(String bank_account) {
        this.bank_account = bank_account;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public void setSwift_code(String swift_code) {
        this.swift_code = swift_code;
    }

    public void setCod_email(String cod_email) {
        this.cod_email = cod_email;
    }

    public void setCurrency_symbol(String currency_symbol) {
        this.currency_symbol = currency_symbol;
    }

    public void setCurrency_short_form(String currency_short_form) {
        this.currency_short_form = currency_short_form;
    }

    public void setSender_email(String sender_email) {
        this.sender_email = sender_email;
    }

    public void setFlat_rate_shipping(String flat_rate_shipping) {
        this.flat_rate_shipping = flat_rate_shipping;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setItem_count(int item_count) {
        this.item_count = item_count;
    }

    public void setCategory_count(int category_count) {
        this.category_count = category_count;
    }

    public void setFollow_count(int follow_count) {
        this.follow_count = follow_count;
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

    public void setCover_image_description(String cover_image_description) {
        this.cover_image_description = cover_image_description;
    }


    public ArrayList<CategoryData> getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(ArrayList<CategoryData> categoryData) {
        this.categoryData = categoryData;
    }

}
