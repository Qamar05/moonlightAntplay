package com.antplay.utils;

public class Const {
    //public static String DEV_URL="http://192.168.0.80:8080";
    //public static String DEV_URL="http://103.182.65.1/api/"; //http://103.182.65.1/api/login/
    public static String DEV_URL="https://uat.antplay.tech/api/"; //http://103.182.65.1/api/login/
    public static String PROD_URL="http://192.168.0.80:8080";
    //public static String PROD_URL="https://fmcg.xaapps.com/";

    public static String emailPattern = "^(?:\\d{10}|\\w+@\\w+\\.\\w{2,3})$";
    public static String passwordRegex = "^(?=.*\\d)(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

    public static String ACCESS_TOKEN="access_token";
    public static String IS_LOGGED_IN="logged_in";
    public static String IS_FIRST_TIME="is_first_time";

    public static String REDIRECT_URL="redirectURL";

    /*****
     * Intent Values
     * ****/
    public static String TERMS_AND_CONDITION_URL="https://antplay.tech/termsAndConditionsForApp";
    public static String PRIVACY_POLICY_URL="https://antplay.tech/privacyPolicyForApp";
    public static String ABOUT_US_URL="https://antplay.tech/aboutUsForApp";
    public static String FAQ_URL="https://antplay.tech/faqForApp";

}
