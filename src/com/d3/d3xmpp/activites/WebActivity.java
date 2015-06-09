/**
 * 
 */
package com.d3.d3xmpp.activites;

import java.util.Calendar;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.util.DES;
import com.d3.d3xmpp.R;

/**
 * @author MZH
 *
 */
public class WebActivity extends BaseActivity {
	@D3View public WebView webView;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_web1);
		initTitle();
		
		long now = Calendar.getInstance().getTimeInMillis();
		String timestamp = String.valueOf(now);
		String sign = DES.encryptDES(String.valueOf(now));
		
		String url = getIntent().getStringExtra("url");

		webView.getSettings().setDefaultTextEncodingName("utf-8"); 
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		//设置WebView属性，能够执行Javascript脚本 
		webView.getSettings().setJavaScriptEnabled(true); 
		webView.loadUrl(url);
		//设置Web视图 
		webView.setWebViewClient(new HelloWebViewClient ()); 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		webView.reload();
	}
	
	
	 //Web视图 
    private class HelloWebViewClient extends WebViewClient { 
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
            view.loadUrl(url); 
            return false; 
        }
        
    } 
}
