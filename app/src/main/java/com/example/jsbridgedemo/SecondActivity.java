package com.example.jsbridgedemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.widget.TextView;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.google.gson.Gson;

/**
 * Created by HP on 2016/12/7.
 */

public class SecondActivity extends Activity {

    TextView textView;
    BridgeWebView bridgeWebView;

    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

         data =  getIntent().getStringExtra("data");

        textView = (TextView) findViewById(R.id.text);
        bridgeWebView = (BridgeWebView) findViewById(R.id.webView);

        textView.setText(data);

        WebSettings setting = bridgeWebView.getSettings();
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);

        bridgeWebView.loadUrl("file:///android_asset/test.html");


        bridgeWebView.addJavascriptInterface(new Object() {

            // 这里我定义了一个拨打的方法
            public void startPhone(String num) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + num));
                startActivity(intent);
            }
        }, "demo1");
    }
}
