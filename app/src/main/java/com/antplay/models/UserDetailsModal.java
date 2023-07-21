package com.antplay.models;

import com.google.gson.annotations.SerializedName;


public class UserDetailsModal {

    @SerializedName("address")
    private String mAddress;
    @SerializedName("age")
    private Object mAge;
    @SerializedName("city")
    private String mCity;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("middle_name")
    private String mMiddleName;
    @SerializedName("phone_number")
    private String mPhoneNumber;
    @SerializedName("pincode")
    private String mPincode;
    @SerializedName("resource_group")
    private String mResourceGroup;
    @SerializedName("state")
    private String mState;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("expire")
    private String mExpire;

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public Object getAge() {
        return mAge;
    }

    public void setAge(Object age) {
        mAge = age;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getMiddleName() {
        return mMiddleName;
    }

    public void setMiddleName(String middleName) {
        mMiddleName = middleName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getPincode() {
        return mPincode;
    }

    public void setPincode(String pincode) {
        mPincode = pincode;
    }

    public String getResourceGroup() {
        return mResourceGroup;
    }

    public void setResourceGroup(String resourceGroup) {
        mResourceGroup = resourceGroup;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getExpire() {
        return mExpire;
    }

    public void setExpire(String mExpire) {
        this.mExpire = mExpire;
    }
}
