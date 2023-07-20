package com.antplay.utils;

public class Const {
    //public static String DEV_URL="http://192.168.0.80:8080";
    //public static String DEV_URL="http://103.182.65.1/api/"; //http://103.182.65.1/api/login/
    public static String DEV_URL="https://uat.antplay.tech/api/"; //http://103.182.65.1/api/login/
    public static String PROD_URL="http://192.168.0.80:8080";
    //public static String PROD_URL="https://fmcg.xaapps.com/";
//    public static String URL =  "https://api.antplay.tech/api/";
    public static String URL =  "https://uat.antplay.tech/api/";

//    public static String emailPattern = "^(?:\\d{10}|\\w+@\\w+\\.\\w{2,3})$";
    public static String emailPattern ="^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";
    public static String passwordRegex = "^(?=.*\\d)(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

    public static String ACCESS_TOKEN="access_token";
    public static String IS_LOGGED_IN="logged_in";
    public static String IS_FIRST_TIME="is_first_time";
    public static String REDIRECT_URL="redirectURL";

    public static String USERNAME="username";
    public static String FIRSTNAME="first_name";
    public static String LASTNAME="last_name";
    public static String EMAIL_ID="email_id";
    public static String PHONE_NUMBER="phone_number";
    public static String STATE="state";
    public static String CITY="city";
    public static String PINCODE="pincode";

    public static final Integer ERROR_CODE_500 = 500;
    public static final String ERROR_CODE_500_SERVER_ERROR = "500 Internal Server Error";
    public static String no_records = "No Records Found";
    public static final Integer ERROR_CODE_400 = 400;
    public static final Integer ERROR_CODE_404 = 404;
    public static final Integer SUCCESS_CODE_200 = 200;
    public static String something_went_wrong = "Something went wrong";







    /*****
     * Intent Values
     * ****/
    public static String TERMS_AND_CONDITION_URL="https://antplay.tech/termsAndConditionsForApp";
    public static String PRIVACY_POLICY_URL="https://antplay.tech/privacyPolicyForApp";
    public static String ABOUT_US_URL="https://antplay.tech/aboutUsForApp";
    public static String FAQ_URL="https://antplay.tech/faqForApp";

}
