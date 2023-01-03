package com.sdk.perelander;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

public class AdsActivity extends Activity {

    private WebView webView;
    LinearLayout linearLayout;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        setupView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupView() {
        webView = findViewById(R.id.webview_connect);
        linearLayout = findViewById(R.id.layout_click);
        CookieManager.getInstance().setAcceptCookie(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                String url = request.getUrl().toString();
                MobInstance mobInstance = Mob.getDefaultInstance();

                if (url.startsWith("tel") || url.startsWith("sms")) {
                    try {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("AdjustSDK", "postDelayed 4 " );
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.setData(Uri.parse(url));
                                startActivity(intent);
                            }
                        }, 700);

                        mobInstance.closeWActivity();
                        finish();

                    } catch (Exception ignored) {
                        mobInstance.closeWActivity();
                        finish();
                    }
                } else if (!url.startsWith("http")) {
                    mobInstance.closeWActivity();
                    finish();
                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        callWebview();
    }

    private void manageInternetCoon() {

        Log.v("AdjustSDK", "No Internet Connection!"  );

        linearLayout.setVisibility(View.VISIBLE);
        Button retryBtn = findViewById(R.id.button_retry);
        retryBtn.setOnClickListener(view -> {
            linearLayout.setVisibility(View.GONE);
            callWebview();
        });

    }

    private void callWebview() {
        if (Utils.isConnected(this)) {
            MobInstance mobInstance = Mob.getDefaultInstance();

            String end_url = mobInstance.getMainU();
            Log.e("AdjustSDK", "end_url: " + end_url );
            webView.loadUrl(end_url);
        } else {
            manageInternetCoon();
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

    @Override
    public void onBackPressed() {
    }
}