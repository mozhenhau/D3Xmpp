package com.d3.d3xmpp.util;

import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.d3.d3xmpp.xmpp.XmppConnection;
import com.d3.d3xmpp.R;

public abstract class LoadThread {

	private ProgressDialog mdialog;
	private Context mcontext;
	Map<String, String> map = null;
	boolean isHint;
	
	public LoadThread(Context context, String urls, Map<String, String> _map) {
		isHint = true;
		mcontext = context;
		map = _map;
		toLoad(urls);

	}

	protected void toLoad(String urls) {
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
				if (isHint) {
					mdialog = ProgressDialog.show(mcontext, mcontext.getResources().getString(R.string.dialog_title),
							mcontext.getResources().getString(R.string.dialog_load_content));
					mdialog.setCancelable(true);
					mdialog.setContentView(R.layout.dialog_loadding);
					mdialog.setIndeterminateDrawable(mcontext.getResources().getDrawable(
							R.drawable.progress_dialog_style));
					// mdialog.setCanceledOnTouchOutside(false);//
					// 设置点击屏幕Dialog不消失
				}
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
				if (isHint && (mdialog == null || !mdialog.isShowing())) {
					return;
				} else {
					try {
						refreshUI(jsonStrings);
						if (isHint && (mdialog != null && mdialog.isShowing())) {
							mdialog.dismiss();
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}

		}.execute(urls);
	}

	protected abstract void refreshUI(String result);
}
