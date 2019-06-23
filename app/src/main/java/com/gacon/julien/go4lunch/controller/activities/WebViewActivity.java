package com.gacon.julien.go4lunch.controller.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gacon.julien.go4lunch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends AppCompatActivity {

    // Web View Layout
    @BindView(R.id.web_view)
    WebView mWebView;
    WebViewClient mWebViewClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        // Initialize ButterKnife
        ButterKnife.bind(this);
        // Configure webview
        this.getWebView();
    }

    /**
     * Create the webView
     */
    private void getWebView() {
        mWebViewClient = new WebViewClient();
        mWebView.setWebViewClient(mWebViewClient);
        String url=getIntent().getStringExtra("url");
        mWebView.loadUrl(url);
    }
}
