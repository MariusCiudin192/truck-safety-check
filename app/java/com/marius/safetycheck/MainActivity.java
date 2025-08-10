package com.marius.safetycheck;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progress;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI dinamic: Progress + WebView (nu mai ai nevoie de layout XML)
        FrameLayout root = new FrameLayout(this);
        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        progress.setIndeterminate(true);
        progress.setVisibility(View.VISIBLE);

        webView = new WebView(this);
        root.addView(webView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = android.view.Gravity.CENTER;
        root.addView(progress, lp);

        setContentView(root);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setDatabaseEnabled(true);

        WebView.setWebContentsDebuggingEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onConsoleMessage(ConsoleMessage cm) {
                android.util.Log.d("WV", cm.message() + " @ " + cm.lineNumber());
                return super.onConsoleMessage(cm);
            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
            }
        });

        // Încarcă HTML-ul local din APK
        webView.loadUrl("file:///android_asset/index.html");

        // Fallback după 1.5s dacă nu s-a încărcat nimic
        webView.postDelayed(() -> {
            if (webView.getContentHeight() == 0) {
                String html = "<!doctype html><html><body style='font-family:sans-serif;padding:24px'>"
                        + "<h2>Safety Check</h2>"
                        + "<p>Fallback: asset-ul nu s-a încărcat.</p>"
                        + "<p>Verifică <code>app/src/main/assets/index.html</code>.</p>"
                        + "</body></html>";
                webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
            }
        }, 1500);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
