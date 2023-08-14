package com.antplay.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestClient {
    public static final String BASE_URL = "https://uat.antplay.tech/v1/api/";

    Context context;
    boolean status;
    int statusCode = 0;
    String responseString;
    File file = null;

    //Config Settings
    int timeOut = 30000;
    int timeOutMultipart = 60000;
    boolean errorLog = true;
    boolean requestLog = true;
    boolean responseLog = true;
    String TAG_RESPONSE = "apis";
    String INTERNET_CONNECTION_ERROR = "Please check internet connection";
    String API_ERROR = "Failed to connect. Please try again later.";


    public RestClient(Context context) {
        this.context = context;
    }

    public static boolean isOnline(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        // should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public RestClient setTimeout(int timeout) {
        this.timeOut = timeout;
        return this;
    }


    //===========================================GET REQUEST ========================================//
    public RestClient getRequest(final String tag, final String getUrl, final ResponseListener responseListener, final ErrorListener errorListener) {

        if (isOnline(context)) {
            HandlerThread handlerThread = new HandlerThread("HandlerThread");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    InputStream is = null;
                    try {
                        URL url = new URL(BASE_URL + getUrl);
                        Log.d(TAG_RESPONSE, "===========" + getUrl + "===========");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setReadTimeout(timeOut);
                        con.setConnectTimeout(timeOut);
                        con.setRequestMethod("GET");
                       /* con.setRequestProperty("Content-Type", "application/json");
                        con.setDoInput(true);*/
                        con.connect();
                        statusCode = con.getResponseCode();

                        Log.d(TAG_RESPONSE, "===========" + tag + "===========");

                        if (statusCode == 200) {
                            is = con.getInputStream();
                            responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);
                        } else {
                            status = false;
                            is = con.getErrorStream();
                            responseString = convertInputStreamToString(is);
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: Unable to connect to server." + statusCode);
                        }
                    } catch (java.net.SocketTimeoutException e) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "Response :- " + "Error: Connection timed out.");
                        responseString = "Connection timed out. Please try again later.";

                    } catch (Exception ex) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                        } catch (IOException ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                        }
                    }

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (status) {
                                if (responseString != null && responseString.length() > 0)
                                    responseListener.onResponse(tag, responseString);
                            } else {
                                if (responseString != null && responseString.length() > 0)
                                    errorListener.onError(tag, responseString, statusCode);
                                else
                                    errorListener.onError(tag, API_ERROR, statusCode);
                            }
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            };

            handler.sendEmptyMessage(0);
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR, statusCode);
        }
        return this;
    }

    //===========================================POST REQUEST=========================================//
    public RestClient postRequestWithoutMethod(final String tag, final String baseUrl, final HashMap<String, String> postParams, final ResponseListenerUpdated responseListenerUpdated, final ErrorListener errorListener) {

        if (isOnline(context)) {
            HandlerThread handlerThread = new HandlerThread("HandlerThread");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    InputStream is = null;
                    try {
                        URL url = new URL(Const.DEV_URL + baseUrl); //+ postUrl
                        Log.d(TAG_RESPONSE, "===========" + baseUrl + "===========");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();


                        con.setDoOutput(true);
                        con.setReadTimeout(timeOut);
                        con.setConnectTimeout(timeOut);
                        con.setRequestMethod("POST");
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        OutputStream os = con.getOutputStream();
                        os.write(getPostDataString(postParams).getBytes());
                        os.write(getPostDataString(postParams).getBytes());
                        os.flush();
                        os.close();

                        if (requestLog && postParams != null)
                            Log.d(TAG_RESPONSE, "===========" + baseUrl + "===========");
                        Log.d(TAG_RESPONSE, "Request :-  " + new Gson().toJson(postParams).toString());

                        statusCode = con.getResponseCode();
                        Log.d(TAG_RESPONSE, "RESPONSECODE :- " + statusCode + "");
                        if (statusCode == 200) {
                            is = con.getInputStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);
                        } else if (statusCode == 201) {
                            is = con.getInputStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);
                        } else if (statusCode == 202) {
                            is = con.getInputStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);
                        } else if (statusCode == 500 || statusCode == 401 || statusCode == 203/*||statusCode==202*/) {
//                            Toast.makeText(context,"",Toast.LENGTH_SHORT).show();
                            status = true;
                            is = con.getErrorStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: Unable to connect to server." + statusCode);
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);

                        }
                    } catch (java.net.SocketTimeoutException e) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "Response :- " + "Error: Connection timed out.");
                        responseString = "Connection timed out. Please try again later.";

                    } catch (Exception ex) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                        } catch (IOException ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                        }
                    }

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (status) {
                                if (responseString != null && responseString.length() > 0)
                                    responseListenerUpdated.onResponse(tag, statusCode, responseString);
                            } else {
                                if (responseString != null && responseString.length() > 0)
                                    errorListener.onError(tag, responseString, statusCode);
                                else
                                    errorListener.onError(tag, API_ERROR, statusCode);
                            }
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            };

            handler.sendEmptyMessage(0);
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR, statusCode);
        }
        return this;
    }

    //===========================================POST REQUEST=========================================//
    public RestClient postRequest(final String tag, final String postUrl, final HashMap<String, String> postParams, final ResponseListener responseListener, final ErrorListener errorListener) {

        if (isOnline(context)) {
            HandlerThread handlerThread = new HandlerThread("HandlerThread");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    InputStream is = null;
                    try {
                        URL url = new URL(BASE_URL + postUrl); //+ postUrl
                        Log.d(TAG_RESPONSE, "===========" + BASE_URL + postUrl + "===========");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setDoOutput(true);
                        con.setReadTimeout(timeOut);
                        con.setConnectTimeout(timeOut);
                        con.setRequestMethod("POST");
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        OutputStream os = con.getOutputStream();
                        os.write(getPostDataString(postParams).getBytes());
                        os.flush();
                        os.close();

                        if (requestLog && postParams != null)
                            Log.d(TAG_RESPONSE, "===========" + BASE_URL + postUrl + "===========");
                        Log.d(TAG_RESPONSE, "Request :-  " + new Gson().toJson(postParams).toString());

                        statusCode = con.getResponseCode();
                        Log.d(TAG_RESPONSE, "RESPONSECODE :- " + statusCode + "");
                        if (statusCode == 200) {
                            is = con.getInputStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);
                        } else if (statusCode == 302) {
                            is = con.getInputStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);
                        } else if (statusCode == 500 || statusCode == 401 || statusCode == 203) {
//                            Toast.makeText(context,"",Toast.LENGTH_SHORT).show();
                            status = true;
                            is = con.getErrorStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: Unable to connect to server." + statusCode);
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);

                        }
                    } catch (java.net.SocketTimeoutException e) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "Response :- " + "Error: Connection timed out.");
                        responseString = "Connection timed out. Please try again later.";

                    } catch (Exception ex) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                        } catch (IOException ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                        }
                    }

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (status) {
                                if (responseString != null && responseString.length() > 0)
                                    responseListener.onResponse(tag, responseString);
                            } else {
                                if (responseString != null && responseString.length() > 0)
                                    errorListener.onError(tag, responseString, statusCode);
                                else
                                    errorListener.onError(tag, API_ERROR, statusCode);
                            }
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            };

            handler.sendEmptyMessage(0);
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR, statusCode);
        }
        return this;
    }

    //===========================================POST REQUEST WITH LOCALISATION HEADER=========================================//
    public RestClient postRequestWithLocalisation(final String tag, final String postUrl, final HashMap<String, String> postParams, final String xLocalisation, final ResponseListener responseListener, final ErrorListener errorListener) {

        if (isOnline(context)) {
            HandlerThread handlerThread = new HandlerThread("HandlerThread");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    InputStream is = null;
                    try {
                        URL url = new URL(BASE_URL + postUrl); //+ postUrl
                        Log.d(TAG_RESPONSE, "===========" + BASE_URL + postUrl + "===========");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();

                        /************ Header Started ************/
                        con.setRequestProperty("Accept", "application/json");
                        con.setRequestProperty("x-localization", xLocalisation);
                        /************ Header end ************/

                        con.setDoOutput(true);
                        con.setReadTimeout(timeOut);
                        con.setConnectTimeout(timeOut);
                        con.setRequestMethod("POST");
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        OutputStream os = con.getOutputStream();
                        os.write(getPostDataString(postParams).getBytes());
                        os.flush();
                        os.close();

                        if (requestLog && postParams != null)
                            Log.d(TAG_RESPONSE, "===========" + BASE_URL + postUrl + "===========");
                        Log.d(TAG_RESPONSE, "Request :-  " + new Gson().toJson(postParams).toString());

                        statusCode = con.getResponseCode();
                        Log.d(TAG_RESPONSE, "RESPONSECODE :- " + statusCode + "");
                        if (statusCode == 200) {
                            is = con.getInputStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);
                        } else if (statusCode == 302) {
                            is = con.getInputStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);
                        } else if (statusCode == 500 || statusCode == 401 || statusCode == 203) {
//                            Toast.makeText(context,"",Toast.LENGTH_SHORT).show();
                            status = true;
                            is = con.getErrorStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: Unable to connect to server." + statusCode);
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);

                        }
                    } catch (java.net.SocketTimeoutException e) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "Response :- " + "Error: Connection timed out.");
                        responseString = "Connection timed out. Please try again later.";

                    } catch (Exception ex) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                        } catch (IOException ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                        }
                    }

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (status) {
                                if (responseString != null && responseString.length() > 0)
                                    responseListener.onResponse(tag, responseString);
                            } else {
                                if (responseString != null && responseString.length() > 0)
                                    errorListener.onError(tag, responseString, statusCode);
                                else
                                    errorListener.onError(tag, API_ERROR, statusCode);
                            }
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            };

            handler.sendEmptyMessage(0);
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR, statusCode);
        }
        return this;
    }

    //===========================================Post Request With Header=========================================//
    public RestClient postRequestWithHeader(final String tag, final String postUrl, final HashMap<String, String> postParams, final String apiToken, final String xLocalisation, final ResponseListener responseListener, final ErrorListener errorListener) {

        if (isOnline(context)) {
            HandlerThread handlerThread = new HandlerThread("HandlerThread");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    InputStream is = null;
                    String authorizationValue = "Bearer " + apiToken;
                    Log.d("auth", authorizationValue);
                    //String auth = "Bearer "+ new String(new Base64().encode(apiToken.getBytes()));
                    try {
                        URL url = new URL(Const.DEV_URL + postUrl); //+ postUrl
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();

                        /************ Header Started ************/
                        con.setRequestProperty("Accept", "application/json");
                        con.setRequestProperty("Authorization", authorizationValue);
                      //  con.setRequestProperty("x-localization", xLocalisation);
                        /************ Header end ************/
                        con.setDoOutput(true);
                        con.setReadTimeout(timeOut);
                        con.setConnectTimeout(timeOut);
                        con.setRequestMethod("POST");
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        OutputStream os = con.getOutputStream();
                        os.write(getPostDataString(postParams).getBytes());
                        os.flush();
                        os.close();

                        if (requestLog && postParams != null)
                            Log.d(TAG_RESPONSE, "===========" + BASE_URL + postUrl + "===========");
                        Log.d(TAG_RESPONSE, "Request :-  " + new Gson().toJson(postParams).toString());

                        statusCode = con.getResponseCode();
                        Log.d(TAG_RESPONSE, "RESPONSECODE :- " + statusCode + "");
                        if (statusCode == 200) {
                            is = con.getInputStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);
                        } else if (statusCode == 302) {
                            is = con.getInputStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);
                        } else if (statusCode == 500 || statusCode == 401 || statusCode == 203) {
//                            Toast.makeText(context,"",Toast.LENGTH_SHORT).show();
                            status = true;
                            is = con.getErrorStream();
                            if (is != null)
                                responseString = convertInputStreamToString(is);
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: Unable to connect to server." + statusCode);
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);

                        }
                    } catch (java.net.SocketTimeoutException e) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "Response :- " + "Error: Connection timed out.");
                        responseString = "Connection timed out. Please try again later.";

                    } catch (Exception ex) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                        } catch (IOException ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                        }
                    }

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (status) {
                                if (responseString != null && !responseString.isEmpty())
                                    responseListener.onResponse(tag, responseString);
                            } else {
                                if (responseString != null && !responseString.isEmpty())
                                    errorListener.onError(tag, responseString, statusCode);
                                else
                                    errorListener.onError(tag, API_ERROR, statusCode);
                            }
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            };

            handler.sendEmptyMessage(0);
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR, statusCode);
        }
        return this;
    }

    //===========================================Multipart REQUEST=========================================//
    public void MultipartRequest(final String tag, final String baseUrl, final HashMap<String, String> postParams, final File file, final String fileKey, final ResponseListenerUpdated responseListener, final ErrorListener errorListener) {
        if (isOnline(context)) {
            if (file != null) {
                final String apiKey = null;
                HandlerThread handlerThread = new HandlerThread("HandlerThread");
                handlerThread.start();
                Handler handler = new Handler(handlerThread.getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {

                        try {
                            String charset = "UTF-8";
                            String requestURL = baseUrl;
                            if (requestLog && postParams != null)
                                Log.d(TAG_RESPONSE, "===========" + tag + "===========");
                            Log.d(TAG_RESPONSE, "Request :-  " + requestURL);
                            Log.d(TAG_RESPONSE, "Request :-  " + new Gson().toJson(postParams).toString());


                            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                            for (HashMap.Entry<String, String> entry : postParams.entrySet())
                                multipart.addFormField(entry.getKey().toString(), entry.getValue().toString());

                            if (file.exists()) {
                                multipart.addFilePart(fileKey, file);
                                responseString = multipart.execute();
                            } else {
                                postRequestWithoutMethod(tag, baseUrl, postParams, responseListener, errorListener);

                            }


                        } catch (Exception ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                        }
                        Handler mainHandler = new Handler(context.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (status) {
                                    if (responseString != null && responseString.length() > 0)
                                        Log.d(TAG_RESPONSE, "Response :- " + responseString);
                                    responseListener.onResponse(tag,statusCode, responseString);
                                } else {
                                    if (responseString != null && responseString.length() > 0)
                                        errorListener.onError(tag, responseString, statusCode);
                                    else
                                        errorListener.onError(tag, API_ERROR, statusCode);
                                }
                            }
                        };
                        mainHandler.post(myRunnable);
                    }
                };

                handler.sendEmptyMessage(0);
            } else {
                postRequestWithoutMethod(tag, baseUrl, postParams, responseListener, errorListener);
            }
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR, statusCode);
        }
    }

    public void MultipartRequestListOfFiles(final String tag, final String baseUrl, final HashMap<String, String> postParams, final HashMap<String, List<File>> filesList, final ResponseListenerUpdated responseListener, final ErrorListener errorListener) {
        if (isOnline(context)) {
            if (filesList != null && filesList.size() > 0) {
                HandlerThread handlerThread = new HandlerThread("HandlerThread");
                handlerThread.start();
                Handler handler = new Handler(handlerThread.getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {

                        try {
                            String charset = "UTF-8";
                            String requestURL = baseUrl;
                            if (requestLog && postParams != null)
                                Log.d(TAG_RESPONSE, "===========" + tag + "===========");
                            Log.d(TAG_RESPONSE, "Request :-  " + new Gson().toJson(postParams).toString());

                            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                            for (HashMap.Entry<String, String> entry : postParams.entrySet())
                                multipart.addFormField(entry.getKey().toString(), entry.getValue().toString());

                            for (String key : filesList.keySet()) {
                                try {
                                    List<File> imageList = filesList.get(key);
                                    for (int i = 0; i < imageList.size(); i++) {
                                        if (imageList.get(i).exists()) {
                                            multipart.addFilePart(key, imageList.get(i));
                                        }
                                    }

                                } catch (NullPointerException npex) {
                                    Log.d(TAG_RESPONSE, "Response :- " + "Error: " + npex.getLocalizedMessage());
                                }

                                if (filesList.get(key) == null) {
                                    postRequestWithoutMethod(tag, baseUrl, postParams, responseListener, errorListener);
                                }
                            }

                            // Adding Header
                          /*  multipart.addHeaderField("Accept","application/json");
                            String authorizationValue = "Bearer "+apiToken;
                            multipart.addHeaderField("Authorization",authorizationValue);*/

                            responseString = multipart.execute();


                        } catch (Exception ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                        }
                        Handler mainHandler = new Handler(context.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (status) {
                                    if (responseString != null && responseString.length() > 0)
                                        responseListener.onResponse(tag, statusCode,responseString);
                                } else {
                                    if (responseString != null && responseString.length() > 0)
                                        errorListener.onError(tag, responseString, statusCode);
                                    else
                                        errorListener.onError(tag, API_ERROR, statusCode);
                                }
                            }
                        };
                        mainHandler.post(myRunnable);
                    }
                };

                handler.sendEmptyMessage(0);
            } else {
                // postRequestWithHeader(tag, url, postParams, apiToken, responseListener, errorListener);
            }
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR, statusCode);
        }
    }


    public void MultipartRequestFilesWithMethod(final String tag, final String baseUrl, final HashMap<String, String> postParams, final HashMap<String, File> files, final ResponseListenerUpdated responseListener, final ErrorListener errorListener) {
        if (isOnline(context)) {
            if (files != null && files.size() > 0) {
                HandlerThread handlerThread = new HandlerThread("HandlerThread");
                handlerThread.start();
                Handler handler = new Handler(handlerThread.getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {

                        try {
                            String charset = "UTF-8";
                            String requestURL = baseUrl;
                            if (requestLog && postParams != null)
                                Log.d(TAG_RESPONSE, "===========" + tag + "===========");
                            Log.d(TAG_RESPONSE, "Request :-  " + new Gson().toJson(postParams).toString());

                            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                            for (HashMap.Entry<String, String> entry : postParams.entrySet())
                                multipart.addFormField(entry.getKey().toString(), entry.getValue().toString());

                            for (String key : files.keySet()) {

                                if (files.get(key).exists()) {
                                    multipart.addFilePart(key, files.get(key));
                                }
                                if (files.get(key) == null) {
                                    postRequestWithoutMethod(tag, baseUrl, postParams, responseListener, errorListener);
                                }
                            }

                            // Adding Header
                          /*  multipart.addHeaderField("Accept","application/json");
                            String authorizationValue = "Bearer "+apiToken;
                            multipart.addHeaderField("Authorization",authorizationValue);*/

                            responseString = multipart.execute();


                        } catch (Exception ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                        }
                        Handler mainHandler = new Handler(context.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (status) {
                                    if (responseString != null && responseString.length() > 0)
                                        responseListener.onResponse(tag, statusCode,responseString);
                                } else {
                                    if (responseString != null && responseString.length() > 0)
                                        errorListener.onError(tag, responseString, statusCode);
                                    else
                                        errorListener.onError(tag, API_ERROR, statusCode);
                                }
                            }
                        };
                        mainHandler.post(myRunnable);
                    }
                };

                handler.sendEmptyMessage(0);
            } else {
                // postRequestWithHeader(tag, url, postParams, apiToken, responseListener, errorListener);
            }
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR, statusCode);
        }
    }


    //===========================================GET REQUEST WITH HEADER ========================================//
    public RestClient getRequestWithHeader(final String tag, final String getUrl, final String postParams, final String apiToken, final ResponseListener responseListener, final ErrorListener errorListener) {

        if (isOnline(context)) {
            HandlerThread handlerThread = new HandlerThread("HandlerThread");

            Handler handler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    InputStream is = null;
                    String authorizationValue = "Bearer " + apiToken;
                    Log.d("auth", authorizationValue);
                    try {
                        URL url = new URL(BASE_URL + getUrl + postParams);
                        Log.d(TAG_RESPONSE, "===========" + getUrl + "===========");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        /************ Header Started ************/
                        con.setRequestProperty("Accept", "application/json");
                        con.setRequestProperty("Authorization", authorizationValue);
                        /************ Header end ************/
                        con.setReadTimeout(timeOut);
                        con.setConnectTimeout(timeOut);
                        con.setRequestMethod("GET");


                       /* con.setRequestProperty("Content-Type", "application/json");
                        con.setDoInput(true);*/
                        con.connect();
                        statusCode = con.getResponseCode();

                        Log.d(TAG_RESPONSE, "===========" + tag + "===========");

                        if (statusCode == 200) {
                            is = con.getInputStream();
                            responseString = convertInputStreamToString(is);
                            status = true;
                            if (responseLog && responseString != null)
                                Log.d(TAG_RESPONSE, "Response :- " + responseString);
                        } else {
                            status = false;
                            is = con.getErrorStream();
                            responseString = convertInputStreamToString(is);
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: Unable to connect to server." + statusCode);
                        }
                    } catch (java.net.SocketTimeoutException e) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "Response :- " + "Error: Connection timed out.");
                        responseString = "Connection timed out. Please try again later.";

                    } catch (Exception ex) {
                        status = false;
                        if (errorLog)
                            Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                        } catch (IOException ex) {
                            status = false;
                            if (errorLog)
                                Log.d(TAG_RESPONSE, "Response :- " + "Error: " + ex.getLocalizedMessage());
                        }
                    }

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (status) {
                                if (responseString != null && responseString.length() > 0)
                                    responseListener.onResponse(tag, responseString);
                            } else {
                                if (responseString != null && responseString.length() > 0)
                                    errorListener.onError(tag, responseString, statusCode);
                                else
                                    errorListener.onError(tag, API_ERROR, statusCode);
                            }
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            };

            handler.sendEmptyMessage(0);
        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR, statusCode);
        }
        return this;
    }


    //===========================================DOWNLOAD REQUEST=========================================//
    public RestClient downloadFile(final String tag, final String postUrl, final HashMap<String, String> postParams, final String fileName, final FileDownloadListener responseListener, final ErrorListener errorListener) {

        if (isOnline(context)) {
            final int BUFFER_SIZE = 4096;
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... params) {
                    InputStream is = null;
                    try {
                        URL url = new URL(BASE_URL + postUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();

                        con.setDoOutput(true);
                        con.setReadTimeout(timeOut);
                        con.setConnectTimeout(timeOut);
                        con.setRequestMethod("POST");
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        OutputStream os = con.getOutputStream();
                        os.write(getPostDataString(postParams).getBytes());
                        os.flush();
                        os.close();

                        if (requestLog && postParams != null)
                            Log.d(TAG_RESPONSE, "===========" + tag + "===========");
                        Log.d(TAG_RESPONSE, "Request :-  " + new Gson().toJson(postParams).toString());

                        statusCode = con.getResponseCode();
                        if (statusCode == 200 /*|| statusCode ==400*/) {
                            is = con.getInputStream();
                            // File downloadsDirfile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            file = new File(context.getFilesDir(), fileName);
                            FileOutputStream outputStream = new FileOutputStream(file);
                            int bytesRead = -1;
                            byte[] buffer = new byte[BUFFER_SIZE];
                            while ((bytesRead = is.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }

                            outputStream.close();
                            is.close();
                            status = true;
                            return "success";
                        } else {
                            status = false;
                            Log.d(TAG_RESPONSE, "Error: Failed to connect to server: " + statusCode);
                            return "Failed to connect. Please try again";
                        }
                    } catch (java.net.SocketTimeoutException e) {
                        status = false;
                        Log.d(TAG_RESPONSE, "Error: Request timed out" + statusCode);
                        return "Connection timed out.Please try again";
                    } catch (Exception ex) {
                        status = false;
                        Log.d(TAG_RESPONSE, "Error: " + ex.getLocalizedMessage());
                        return "An error occurred. Please try again";
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                        } catch (IOException e) {
                            status = false;
                            Log.d(TAG_RESPONSE, "Error: " + e.getLocalizedMessage());
                            return "An error occurred. Please try again";
                        }
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    if (status) {
                        if (responseLog && result != null && result.equalsIgnoreCase("success"))
                            Log.d(TAG_RESPONSE, "Response :- " + result);
                        responseListener.onResponse(tag, result, file);
                    } else {
                        if (errorLog && result != null)
                            Log.d(TAG_RESPONSE, "Response :- " + result);
                        errorListener.onError(tag, result, statusCode);
                    }
                }
            }.execute();

        } else {
            errorListener.onError(tag, INTERNET_CONNECTION_ERROR, statusCode);
        }
        return this;
    }


    //=========================================ConnectionUtils=====================================================//
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder result = null;
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            result = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                result.append(line).append('\n');
            }
            return result.toString();
        } catch (Exception ex) {
            return "";
        }

    }


    public String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public RestClient enableErrorLog() {
        errorLog = true;
        return this;
    }

    public RestClient enableResponseLog() {
        responseLog = true;
        return this;
    }

    public interface ResponseListener {
        void onResponse(String tag, String response);
    }

    public interface ResponseListenerUpdated {
        void onResponse(String tag, int responseCode, String response);
    }

    public interface FileDownloadListener {
        void onResponse(String tag, String response, File file);
    }

    public interface ErrorListener {
        void onError(String tag, String errorMsg, long statusCode);
    }

    class MultipartUtility {
        private static final String LINE_FEED = "\r\n";
        private final String boundary;
        private HttpURLConnection httpConn;
        private String charset;
        private OutputStream outputStream;
        private PrintWriter writer;
        String authorizationValue;

        public MultipartUtility(String requestURL, String charset)
                throws IOException {
            this.charset = charset;
            //authorizationValue = "Bearer " + authKey;

            boundary = "-------" + System.currentTimeMillis() + "-------";
            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setReadTimeout(timeOutMultipart);
            httpConn.setConnectTimeout(timeOutMultipart);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            httpConn.setRequestProperty("Accept", "application/json");
            // httpConn.setRequestProperty("Authorization", authorizationValue);
            // httpConn.setRequestProperty("x-localization", xLocalization);
            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                    true);
        }

        public void addFormField(String name, String value) {
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
            // writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(value).append(LINE_FEED);
            writer.flush();
        }

        public void addFilePart(String fieldName, File uploadFile)
                throws IOException {
            if (uploadFile != null) {
                String fileName = uploadFile.getName();
                writer.append("--" + boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
                writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
                writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.flush();

                String str = URLConnection.guessContentTypeFromName(fileName);

                FileInputStream inputStream = new FileInputStream(uploadFile);
                long len = inputStream.available();
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                inputStream.close();
                writer.append(LINE_FEED);
                writer.flush();
            }

        }


        public void addHeaderField(String name, String value) {
            writer.append(name + ": " + value).append(LINE_FEED);
            writer.flush();
        }


        public String execute() throws IOException {
            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            statusCode = httpConn.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                reader.close();
                httpConn.disconnect();
                String responseStr = stringBuilder.toString();
//                /Log.d(TAG_RESPONSE, "Response :- " + responseStr);
                status = true;
                return responseStr;
            } else {
                status = false;
                if (errorLog)
                    Log.d(TAG_RESPONSE, "Response :- " + "Error: Unable to connect to server." + statusCode);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        httpConn.getErrorStream()));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                reader.close();
                httpConn.disconnect();
                String responseStr = stringBuilder.toString();

                Log.d(TAG_RESPONSE, "Response :- " + responseStr);
                return null;
            }

        }
    }


}
