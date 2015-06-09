package com.d3.d3xmpp.util;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.d3.d3xmpp.R;

public abstract class XmppLoadThread {

	boolean isHint;
	ProgressDialog mdialog;
	private Context c;
//	private ExecutorService FULL_TASK_EXECUTOR;

	@SuppressLint("NewApi")
	public XmppLoadThread(Context _mcontext) {
		isHint = true;
		c = _mcontext;
//		FULL_TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool();
		new AsyncTask<Void, Integer, Object>() {

			@Override
			protected Object doInBackground(Void... arg0) {
				return load();
			}

			@Override
			protected void onPostExecute(Object result) {
				if (isHint && (mdialog == null || !mdialog.isShowing())) {
					return;
				} else {
					try {
						result(result);
						if (isHint && (mdialog != null && mdialog.isShowing())) {
							mdialog.dismiss();

						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			protected void onPreExecute() {
				if (isHint) {
					try {
						mdialog =  ProgressDialog.show(c, c.getResources().getString(R.string.dialog_title), c
								.getResources().getString(R.string.dialog_load_content));
						mdialog.setCancelable(true);
						mdialog.setContentView(R.layout.dialog_loadding);
						mdialog.setIndeterminateDrawable(c.getResources().getDrawable(R.drawable.progress_dialog_style));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}.execute();
	}

	protected abstract Object load();

	protected abstract void result(Object object);

}
