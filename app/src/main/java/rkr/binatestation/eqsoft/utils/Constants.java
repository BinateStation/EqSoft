package rkr.binatestation.eqsoft.utils;

/**
 * Created by RKR on 02-04-2016.
 * Constants.
 */
public final class Constants {

    /**
     * The Constant that are used in volley for socket timeout
     */
    public static final int socketTimeout = 30000;//30 seconds - change to what you want
    /**
     * The Constants that are used in server APIs
     */
    public static final String CATEGORIES = "categories";
    public static final String GALLERY = "gallery";
    public static final String LOGIN = "login";
    public static final String REGISTER = "register";
    public static final String GALLERY_UPLOAD = "gallery/upload";
    public static final String PROFILE = "profile";
    public static final String UPDATE = "update";
    public static final String CHANGE_PASSWORD = "changepassword";
    public static final String MOBILE_REGISTER = "mobile/register";
    /**
     ***********************************************************************************************
     */

    /**
     * The Constants that are used in SharedPreferences
     */
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_SYNC_TYPE = "sync_type";
    public static final String KEY_CUSTOMER = "customer";
    public static final String KEY_LAST_SELECTED_CUSTOMER = "last_selected_customer_code";

    /**
     * **********************************************************************************************
     */
    public static final Integer REQUEST_CODE_CUSTOMER_ORDER_SUMMARY = 100;
    public static final Integer REQUEST_CODE_CUSTOMER_PRODUCT = 101;

}
