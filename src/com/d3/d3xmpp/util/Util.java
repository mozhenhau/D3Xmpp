package com.d3.d3xmpp.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.d3.d3xmpp.activites.MainActivity;

public class Util {
	private static Util util;
	public static int flag = 0;
	private double r = 6371.004;

	private Util() {

	}

	public static Util getInstance() {
		if (util == null) {
			util = new Util();
		}
		return util;
	}

	/**
	 * 判断是否有sdcard
	 * 
	 * @return
	 */
	public boolean hasSDCard() {
		boolean b = false;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			b = true;
		}
		return b;
	}

	/**
	 * 得到sdcard路径
	 * 
	 * @return
	 */
	public String getExtPath() {
		String path = "";
		if (hasSDCard()) {
			path = Environment.getExternalStorageDirectory().getPath();
		}
		return path;
	}

	/**
	 * 得到/data/data/com.d3studio.together目录
	 * 
	 * @param mActivity
	 * @return
	 */
	public String getPackagePath(Context mActivity) {
		return mActivity.getFilesDir().toString();
	}

	/**
	 * 根据url得到图片
	 * 
	 * @param url
	 * @return
	 */
	public String getImageName(String url) {
		String imageName = "";
		if (url != null) {
			imageName = url.substring(url.lastIndexOf("/") + 1);
		}
		return imageName;
	}

	/**
	 * 根据经纬度获取距离
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	public String getDistance(double lat1, double lon1, double lat2, double lon2) {
		String string;

		//上传位置失败时===  0.0没办法，百度地图的蛋疼设定，唯有丢失非洲用户啦
		if (lat2 == 0 && lon2 == 0) {
			string = "暂无数据";
		} else {
			double distance = 0;
			distance = 2
					* r
					* Math.asin(Math.sqrt(Math.pow(Math.sin((lat1 - lat2) / 2),
							2)
							+ Math.cos(lat1)
							* Math.cos(lat2)
							* Math.pow(Math.sin((lon1 - lon2) / 2), 2)));
			// 保留两位小数
			DecimalFormat df = new DecimalFormat("##.##");
			string = df.format(distance)+"km";
		}
		return string;
	}

	/**
	 * 判断手机号码的正确性
	 * 
	 * @param mobiles
	 * @return
	 */
	public boolean isMobileNumber(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	/**
	 * 判断邮箱的正确性
	 * 
	 * @param mobiles
	 * @return
	 */
	public boolean isEmail(String email) {
		Pattern p = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 不能全是相同的数字或者字母
	 */
	public boolean equalStr(String numOrStr) {
		boolean flag = true;
		char str = numOrStr.charAt(0);
		for (int i = 0; i < numOrStr.length(); i++) {
			if (str != numOrStr.charAt(i)) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	// 不能是连续的数字--递增
	public boolean isOrderNumeric(String numOrStr) {
		boolean flag = true;
		boolean isNumeric = true;
		for (int i = 0; i < numOrStr.length(); i++) {
			if (!Character.isDigit(numOrStr.charAt(i))) {
				isNumeric = false;
				break;
			}
		}
		if (isNumeric) {
			for (int i = 0; i < numOrStr.length(); i++) {
				if (i > 0) {
					int num = Integer.parseInt(numOrStr.charAt(i) + "");
					int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") + 1;
					if (num != num_) {
						flag = false;
						break;
					}
				}
			}
		} else {
			flag = false;
		}
		return flag;
	}

	// 不能是连续的数字--递减
	public boolean isOrderNumeric_(String numOrStr) {
		boolean flag = true;
		boolean isNumeric = true;
		for (int i = 0; i < numOrStr.length(); i++) {
			if (!Character.isDigit(numOrStr.charAt(i))) {
				isNumeric = false;
				break;
			}
		}
		if (isNumeric) {
			for (int i = 0; i < numOrStr.length(); i++) {
				if (i > 0) {
					int num = Integer.parseInt(numOrStr.charAt(i) + "");
					int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") - 1;
					if (num != num_) {
						flag = false;
						break;
					}
				}
			}
		} else {
			flag = false;
		}
		return flag;

	}
	
	
	// 遍历布局，并禁用所有子控件 -- 不禁止textview
	public static void disableSubControls(ViewGroup viewGroup) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View v = viewGroup.getChildAt(i);
			if (v instanceof ViewGroup) {
				if (v instanceof Spinner) {
					Spinner spinner = (Spinner) v;
					spinner.setClickable(false);
					spinner.setEnabled(false);

				} else if (v instanceof ListView) {
					((ListView) v).setClickable(false);
					((ListView) v).setEnabled(false);

				} else {
					disableSubControls((ViewGroup) v);
				}
			} else if (v instanceof EditText) {
				((EditText) v).setEnabled(false);
				((EditText) v).setClickable(false);

			} else if (v instanceof Button) {
				((Button) v).setEnabled(false);
			}
		}
	}

	// 遍历布局，并禁用所有子控件  -- 不禁止textview
	public static void ableSubControls(ViewGroup viewGroup) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View v = viewGroup.getChildAt(i);
			if (v instanceof ViewGroup) {
				if (v instanceof Spinner) {
					Spinner spinner = (Spinner) v;
					spinner.setClickable(true);
					spinner.setEnabled(true);

				} else if (v instanceof ListView) {
					((ListView) v).setClickable(true);
					((ListView) v).setEnabled(true);

				} else {
					ableSubControls((ViewGroup) v);
				}
			} else if (v instanceof EditText) {
				((EditText) v).setEnabled(true);
				((EditText) v).setClickable(true);

			} else if (v instanceof Button) {
				((Button) v).setEnabled(true);
			}
		}
	}
	

	 // 隐藏手机键盘  
	public static void hideIM(Context context,View edt) {  
     try {  
         InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);  
         IBinder windowToken = edt.getWindowToken();  
         if (windowToken != null) {  
             im.hideSoftInputFromWindow(windowToken, 0); 
//             System.out.println("done");
         }  
     } catch (Exception e) {  
//     	System.out.println("失败");
     }  
 }
}
