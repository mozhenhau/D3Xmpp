package com.d3.d3xmpp.activites;


import java.io.File;

import android.R.integer;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.util.ImageUtil;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.R;

public class PicSrcPickerActivity extends Activity {
	
	RelativeLayout fromGallery,fromCamera,picView;
	public static String img_path;  //拍照的话保存路径
	public String path;     //最终获得的图片路径
	public String img_Name;
	private static final int CHOOSE_PICTURE = 0;
	private static final int TAKE_PICTURE = 1;
	private static final int MODIFY_FINISH =  2;
	public static final int TAKE_PIC = 1;
	public static final int CHOSE_PIC = 2;
	public static final int CROP = 3;
	
	public static float WIDTH_PROPOR = 1.0f;
	private int type = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pic_picker);
		picView = (RelativeLayout)findViewById(R.id.picView);
        WIDTH_PROPOR = getIntent().getFloatExtra("width", 1.0f);
        
        
        if (savedInstanceState != null)  ////判断 activity被销毁后 有没有数据被保存下来
		{
			path = savedInstanceState.getString("imgPath");
			File mFile = new File(path);
			System.out.println("拍摄进来了");
			if (mFile.exists()) {
				Intent picIntent = new Intent();
				picIntent.putExtra("imgName", savedInstanceState.getString("imgName"));
				picIntent.putExtra("base64String", ImageUtil.getBase64StringFromFile(path));
				picIntent.putExtra("imgPath", path);
				setResult(Activity.RESULT_OK, picIntent);
				finish();
			} else {
				Tool.initToast(getApplicationContext(), "拍摄失败");
			}
		}
        else{
	        String path = Constants.SAVE_IMG_PATH;
			File filePath = new File(path);
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			img_Name = "t"+ String.valueOf(System.currentTimeMillis()).substring(5)+".jpg";
			img_path = filePath + "/"+ img_Name;
			fromGallery = (RelativeLayout) findViewById(R.id.from_gallery);
			fromCamera = (RelativeLayout) findViewById(R.id.from_camera);
			
			fromGallery.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					goChosePic();
				}
			});
			fromCamera.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					goTakePic();
				}
			});
			
			type = getIntent().getIntExtra("type", 0);
			if (type == TAKE_PIC) {
				goTakePic();
				picView.setVisibility(View.GONE);
			}
			else if (type == CHOSE_PIC) {
				goChosePic();
				picView.setVisibility(View.GONE);
			}
        }
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
        	switch (requestCode) {
			case CHOOSE_PICTURE:
				if (data != null) {
					Uri uri = data.getData();
					if (!TextUtils.isEmpty(uri.getAuthority())) {
						Cursor cursor = getContentResolver().query(uri,
								new String[] { MediaStore.Images.Media.DATA },
								null, null, null);
						if (null == cursor) {
							Tool.initToast(getApplicationContext(), "图片没找到");
							return;
						}
						cursor.moveToFirst();
						String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
						cursor.close();
						Intent intent = new Intent(this, CropImageActivity.class);
						intent.putExtra("path", path);
						startActivityForResult(intent, MODIFY_FINISH);
					} else {
						Intent intent = new Intent(this, CropImageActivity.class);
						intent.putExtra("path", uri.getPath());
						startActivityForResult(intent, MODIFY_FINISH);
					}
				}
				break;
			case TAKE_PICTURE:
//				File f = new File(img_path);
				Intent intent = new Intent(this, CropImageActivity.class);
				intent.putExtra("path", img_path);
				startActivityForResult(intent, MODIFY_FINISH);
				break;
			case MODIFY_FINISH:
				if (data != null) {
					path = data.getStringExtra("path");
					try {
						Intent picIntent = new Intent();
						picIntent.putExtra("imgName", img_Name);
						picIntent.putExtra("base64String", ImageUtil.getBase64StringFromFile(path));
						picIntent.putExtra("imgPath", path);
						setResult(RESULT_OK, picIntent);
						finish();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;

			default:
				break;
        	}
        }
        else if (resultCode == RESULT_CANCELED && (type == TAKE_PIC || type == CHOSE_PIC)) {
        	finish();
		}
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("imgName", img_Name);
		outState.putString("imgPath", path);
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// Nothing need to be done here
		}else {
			// Nothing need to be done here
		}
	}
	
	@Override
	protected void onDestroy() {
		WIDTH_PROPOR = 1.0f;
		super.onDestroy();
	}
	
	private void goTakePic(){
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				Intent picIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				File f = new File(img_path);
				// localTempImgDir和localTempImageFileName是自己定义的名字
				Uri u = Uri.fromFile(f);
				picIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
				picIntent.putExtra(MediaStore.EXTRA_OUTPUT, u);
				startActivityForResult(picIntent, TAKE_PICTURE);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void goChosePic(){
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent,CHOOSE_PICTURE);
	}
	
}
