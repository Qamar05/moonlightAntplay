package com.antplay.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.antplay.R;
import com.antplay.utils.Const;

public class GeneralWebViewActivity extends Activity {
    WebView webView;
    String redirectURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_web_view);

        if (getIntent().hasExtra(Const.REDIRECT_URL)){
            redirectURL = getIntent().getStringExtra(Const.REDIRECT_URL);
        }
        webView = findViewById(R.id.webViewGeneral);
        webView.loadUrl(redirectURL);
        // this will enable the javascript.
        webView.getSettings().setJavaScriptEnabled(true);
        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        webView.setWebViewClient(new WebViewClient());
    }
}