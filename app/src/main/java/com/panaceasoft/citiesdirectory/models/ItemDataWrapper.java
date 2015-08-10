package com.panaceasoft.citiesdirectory.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Panacea-Soft on 19/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class ItemDataWrapper implements Serializable{

    private ItemData itemData;

    public ItemDataWrapper(ItemData it) {
        this.itemData = it;
    }

    public ItemData getItem() {
        return this.itemData;
    }

}
