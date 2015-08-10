package com.panaceasoft.citiesdirectory;

import android.app.Application;

import org.json.JSONObject;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class Config extends Application{

    // Is this an internal dogfood build?
    public static final boolean IS_DOGFOOD_BUILD = false;

    public static final String base_url = "http://www.panacea-soft.com";

    public static final String admob_key = "32dfsdfXKDfXFdXFdfd";


    public static  JSONObject apiResponse = null;

    public static final double REGION_LAT = 1.454201;
    public static final double REGION_LNG = 103.817394;

    public static final String MSG_KEY = "m";
    public static final String APP_API_URL = "http://www.panacea-soft.com/citiesdirectory/index.php";
    //public static final String APP_API_URL = "http://192.168.43.52:7777/citiesdirectory/index.php";
    public static final int PAGINATION = 3;


    public static final String APP_IMAGES_URL = "http://www.panacea-soft.com/citiesdirectory/uploads/";
    //public static final String APP_IMAGES_URL = "http://192.168.43.52:7777/citiesdirectory/uploads/";

    public static final String GET_ALL = "/rest/cities/get";

    public static final String ITEMS_BY_SUB_CATEGORY = "/rest/items/get/city_id/";

    public static final String ITEMS_BY_ID = "/rest/items/get/id/";

    public static final String SEARCH_BY_GEO = "/rest/cities/search_by_geo/miles/";

    public static final String POST_ITEM_INQUIRY = "/rest/items/inquiry/id/";

    public static final String POST_SHOP_INQUIRY = "/rest/cities/contactus/city_id/";

    public static final String POST_USER_LOGIN = "/rest/appusers/login";

    public static final String POST_REVIEW = "/rest/items/review/id/";

    public static final String POST_USER_REGISTER = "/rest/appusers/add/";

    public static final String POST_USER_UPDATE = "/rest/appusers/update/id/";

    public static final String POST_IMAGE_PROFILE = "/rest/images/upload/userId/";

    public static final String POST_ITEM_LIKE = "/rest/items/like/id/";

    public static final String POST_ITEM_FAVOURITE = "/rest/items/favourite/id/";

    public static final String POST_ITEM_SEARCH = "/rest/items/search/city_id/";

    public static final String GET_FORGOT_PASSWORD = "/rest/appusers/reset/email/";

    public static final String GET_FAVOURITE_ITEMS = "/rest/items/user_favourites/user_id/";


    //http://192.168.43.52:7777/restaurateur/index.php/rest/items/get/city_id/1/sub_cat_id/1/item/all/count/5/from/0


    public static JSONObject getApiResponse() {
        return apiResponse;
    }

    public static void setApiResponse(JSONObject apiResponse) {
        Config.apiResponse = apiResponse;
    }
}
