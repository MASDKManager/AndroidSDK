package com.sdk.perelander;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NotificationActivity extends Activity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        webView = (WebView) findViewById(R.id.notification_webview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(" ");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        callWebview();
    }

    private void callWebview() {
        if (Utils.isConnected(this)) {
            MobInstance mobInstance = Mob.getDefaultInstance();

            String end_url = mobInstance.getMainU();
            Log.e("AdjustSDK", "end_url: " + end_url );
            webView.loadUrl(end_url);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.loadUrl("about:blank");
    }

}



