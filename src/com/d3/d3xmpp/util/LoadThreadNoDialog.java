package com.d3.d3xmpp.util;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.xmpp.XmppConnection;

public abstract class LoadThreadNoDialog {
	private Context mcontext;
	Map<String, String> map = null;
	
	public LoadThreadNoDialog(Context context, String urls, Map<String, String> _map) {
		mcontext = context;
		map = _map;
		toLoad(urls);
	}

	protected void toLoad(String urls) {
		if (mcontext!=null) {
			ConnectivityManager cwjManager = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cwjManager.getActiveNetworkInfo();
			if (info == null) {
				Tool.initToast(mcontext, mcontext.getString(R.string.net_error));
				return;
			}

			if (((Activity) mcontext).isFinishing()) {
				return;
			}
			new AsyncTask<String, Integer, String>() {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
				}

				@Override
				protected String doInBackground(String... params) {
					if (TextUtils.isEmpty(params[0])) {
						return null;
					}
					String result = "";
					try {
						result = XmppConnection.requestService(params[0], map);
					} catch (Exception e) {
						e.printStackTrace();
						// MyTools.writerLog(MyTools.getExceptionString(e));
					}
					return result;
				}

				@Override
				protected void onPostExecute(String jsonStrings) {
					try {
						refreshUI(jsonStrings);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}

			}.execute(urls);
		}
	}

	protected abstract void refreshUI(String result);
}
