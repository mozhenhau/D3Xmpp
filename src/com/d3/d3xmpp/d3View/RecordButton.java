
package com.d3.d3xmpp.d3View;

 
import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.util.Tool;

@SuppressLint("NewApi")
public class RecordButton extends Button  {

	public RecordButton(Context context) {
		super(context);
		init();
	}

	public RecordButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setSavePath(String path) {
		File filePath = new File(path);
		if (!filePath.exists()) {
			File file2 = new File(path.substring(0, path.lastIndexOf("/") + 1));
			file2.mkdirs();
		}
		mFileName = path;
	}

	public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
		finishedListener = listener;
	}

	private String mFileName = null;
	private OnFinishedRecordListener finishedListener;
	private static long startTime;
	private Dialog recordIndicator;
	private static int[] res = { R.drawable.mic_2, R.drawable.mic_3,
			R.drawable.mic_4, R.drawable.mic_5 };
	private static ImageView view;
	private static TextView duraView;
	private MediaRecorder recorder;
	private ObtainDecibelThread thread;
	private Handler volumeHandler;
	private static final int MIN_INTERVAL_TIME = 1*1000;// 2s 最短
	public final static int MAX_TIME = 60*1000 + 500;// 20秒，最长
	private final String  SAVE_PATH = Constants.SAVE_SOUND_PATH;
	
	private float y ; 
	
	private void init() {
		volumeHandler = new ShowVolumeHandler();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		y = event.getY();
		if(y<0)
			view.setImageResource(R.drawable.mic_cancel);
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			setText("松开发送");
			initDialogAndStartRecord();
			break;
		case MotionEvent.ACTION_UP:
			this.setText("按住录音");
			if(y>=0 && (System.currentTimeMillis() - startTime <= MAX_TIME)){
				finishRecord();
			}else if(y<0){  //当手指向上滑，会cancel
				cancelRecord();
			}
			break;
		case MotionEvent.ACTION_CANCEL: // 异常
			cancelRecord();
			break;
		}

		return true;
	}
	
	private void initDialogAndStartRecord() {
		startTime = System.currentTimeMillis();
		recordIndicator = new Dialog(getContext(),
				R.style.like_toast_dialog_style);
		view = new ImageView(getContext());
		view.setImageResource(R.drawable.mic_2);
		recordIndicator.setContentView(view, new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		
		duraView = new TextView(getContext());
		duraView.setText("  0\"  最长20\"");
		duraView.setTextSize(15);
		recordIndicator.addContentView(duraView,new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		
		recordIndicator.setOnDismissListener(onDismiss);
		LayoutParams lp = recordIndicator.getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;

		startRecording();
		recordIndicator.show();
	}

	private void finishRecord() {
		stopRecording();
		recordIndicator.dismiss();

		long intervalTime = System.currentTimeMillis() - startTime;
		if (intervalTime < MIN_INTERVAL_TIME) {
			Tool.initToast(getContext(), "时间太短！");
			File file = new File(mFileName);
			file.delete();
			return;
		}

		if (finishedListener != null)
			finishedListener.onFinishedRecord(mFileName,(int) (intervalTime/1000));
	}

	public void cancelRecord() {
		stopRecording();
		recordIndicator.dismiss();
//		MyToast.makeText(getContext(), "取消录音！", Toast.LENGTH_SHORT);
		File file = new File(mFileName);
		file.delete();
	}

	private void startRecording() {
		// save path
		StringBuilder path = new StringBuilder(SAVE_PATH)
				.append("/tmp_sound_").append(System.currentTimeMillis()).append(".amr");
		setSavePath(path.toString());
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setAudioChannels(1);
		recorder.setAudioEncodingBitRate(4000);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		//recorder.setVideoFrameRate(4000);
		recorder.setOutputFile(mFileName);

		try {
			recorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}

		recorder.start();
		thread = new ObtainDecibelThread();
		thread.start();
		Vibrator vib = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);  //震动提醒
	    vib.vibrate(100); 
	}

	private void stopRecording() {
		if (thread != null) {
			thread.exit();
			thread = null;
		}
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
	}

	private class ObtainDecibelThread extends Thread {

		private volatile boolean running = true;

		public void exit() {
			running = false;
		}

		@Override
		public void run() {
			while (running) {
				if (recorder == null || !running) {
					break;
				}
				int x = recorder.getMaxAmplitude();
				if (x != 0 && y>=0) {
					int f = (int) (10 * Math.log(x) / Math.log(10));
					if (f < 26)
						volumeHandler.sendEmptyMessage(0);
					else if (f < 32)
						volumeHandler.sendEmptyMessage(1);
					else if (f < 38)
						volumeHandler.sendEmptyMessage(2);
					else
						volumeHandler.sendEmptyMessage(3);
				}

				volumeHandler.sendEmptyMessage(-1);
				if(System.currentTimeMillis() - startTime > MAX_TIME){
					finishRecord();
				}
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private OnDismissListener onDismiss = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {
			stopRecording();
		}
	};

	static class ShowVolumeHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == -1)
				duraView.setText("  "+(System.currentTimeMillis() - startTime)/1000+ "\"  最长60\"");
			else
				view.setImageResource(res[msg.what]);
		}
	}

	public interface OnFinishedRecordListener {
		public void onFinishedRecord(String audioPath,int time);
	}

	
	 
	
	//private  boolean 
}
