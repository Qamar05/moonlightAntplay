package com.antplay.utils;

public class Const {
    //public static String DEV_URL="http://192.168.0.80:8080";
    //public static String DEV_URL="http://103.182.65.1/api/"; //http://103.182.65.1/api/login/
     //http://103.182.65.1/api/login/
    public static String PROD_URL="http://192.168.0.80:8080";
    //public static String PROD_URL="https://fmcg.xaapps.com/";
//     public static String DEV_URL =  "https://api.antplay.tech/api/";

//    public static String URL =  "https://uat.antplay.tech/v1/api/";
//    public static String DEV_URL="https://uat.antplay.tech/v1/api/";
    public static String URL =  "https://api.antplay.tech/v1/api/";
    public static String DEV_URL="https://api.antplay.tech/v1/api/";

//    public static String emailPattern = "^(?:\\d{10}|\\w+@\\w+\\.\\w{2,3})$";
    public static String emailPattern ="^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";
    public static String passwordRegex = "^(?=.*\\d)(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
    public static String phoneRegex = "^[1-9][0-9]{9,10}$";
    public static String ACCESS_TOKEN="access_token";
    public static String IS_LOGGED_IN="logged_in";
    public static String IS_SHUT_DOWN="shut_down";
    public static String SHOW_OVERLAY="show_overlay";
    public static String IS_VM_CONNECTED="vm_connected";
    public static String IS_VM_DISCONNECTED="vm_disconnected";
    public static String SAVE_DETAILS="save_details";

    public static String IS_STARTVM="startvm";
    public static String IS_FIRST_TIME="is_first_time";
    public static String REDIRECT_URL="redirectURL";

    public static String ANDROID ="android";

    public static String USERNAME="username";
    public static String FIRSTNAME="first_name";

    public static String LASTNAME="last_name";
    public static String EMAIL_ID="email_id";
    public static String PHONE_NUMBER="phone_number";
    public static String STATE="state";
    public static String ADDRESS="address";

    public static String CITY="city";
    public static String PINCODE="pincode";

    public static final String city_should_not_empty = "City should not be empty ";

    public static final String address_should_not_empty = "Address should not be empty";
    public static final String enter_valid_picCode = "Enter 6 digit Valid PinCode ";

    public static String USER_EXPIRY_DATE="userExpiryDate";
    public static String pinCodeRegex = "^[1-9][0-9]{5}$";
    public static final String profile_updated_success = "Profile Updated Successfully ";
    public static final String enter_valid_data = "Enter Valid Data ";
    public static final Integer ERROR_CODE_500 = 500;
    public static String no_records = "No Records Found";
    public static final Integer ERROR_CODE_400 = 400;
    public static final Integer ERROR_CODE_404 = 404;

    public static final Integer SUCCESS_CODE_200 = 200;
    public static String something_went_wrong = "Something went wrong";

    /*****
     * Intent Values
     * ****/

//    public static String TERMS_AND_CONDITION_URL="https://antplay.tech/termsAndConditionsForApp";
//    public static String PRIVACY_POLICY_URL="https://antplay.tech/privacyPolicyForApp";
//    public static String ABOUT_US_URL="https://antplay.tech/aboutUsForApp";
//    public static String WEBSITE_URL="https://antplay.tech/";
//    public static String DISCORD_URL="https://discord.gg/vGHsh8MYXX";
//    public static String INSTAGRAM_URL="https://www.instagram.com/antplay.tech/";
//    public static String FAQ_URL="https://antplay.tech/faqForApp";


    public static String TERMS_AND_CONDITION_URL="https://antplay.tech/terms-and-conditions";
    public static String PRIVACY_POLICY_URL="https://antplay.tech/privacy-policy";
    public static String ABOUT_US_URL="https://antplay.tech/contact-us";
    public static String WEBSITE_URL="https://antplay.tech/";
    public static String DISCORD_URL="https://discord.gg/vGHsh8MYXX";
    public static String INSTAGRAM_URL="https://www.instagram.com/antplay.tech/";
    public static String FAQ_URL="https://antplay.tech/faq";

    public static String VMID="vmId";
    public static String STARTBtnStatus = "start_btnStatus";
    public static String connectbtnVisible = "connectbtnVisible";
    public static String FIRSTTIMEVMTIMER = "firsttimevmtimer";
    public static String FIRSTTIMEDIALOG = "firstTimeDialog";

    public static String startTime = "starttime";
    public static String middleTIme = "middleTime";
    public static String FIRST_TIME_PAYMENT = "firsttimepayment";
    public static String FIRSTTIMESTARTVMAPI = "firstTimeStartVmApi";

    public static String isSUBScriptionScreen = "isSUBScriptionScreen";
    public static String PAYMENT_STATUS = "payment_status";
    public static String LOGIN_EMAIL = "login_email";



}
