/**
 * 
 */
package com.d3.d3xmpp.activites;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.ImgConfig;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.model.User;
import com.d3.d3xmpp.util.CircularImage;
import com.d3.d3xmpp.util.FormatTools;
import com.d3.d3xmpp.util.JsonUtil;
import com.d3.d3xmpp.util.MyAndroidUtil;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.util.XmppLoadThread;
import com.d3.d3xmpp.xmpp.XmppConnection;
import com.d3.d3xmpp.R;

/**
 * @author MZH
 *
 */
public class LoginActivity extends BaseActivity implements OnCheckedChangeListener, TextWatcher {
	@D3View(click="onClick") TextView registBtn,changePwdBtn;
	@D3View(click="onClick") Button loginBtn;
	@D3View CheckBox checkBox;
	@D3View TextView nameText,pwdText;
//	@D3View CircularImage headImg;
	private String name,pwd;
	private boolean isChecked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acti_login);
		initTitle();
		isChecked = MyApplication.sharedPreferences.getBoolean(Constants.LOGIN_CHECK, false);
		checkBox.setOnCheckedChangeListener(this);
		checkBox.setChecked(isChecked);
		nameText.addTextChangedListener(this);
		
		//已登录过,自动登录
		name = MyApplication.sharedPreferences.getString(Constants.LOGIN_ACCOUNT, null);
		pwd = MyApplication.sharedPreferences.getString(Constants.LOGIN_PWD, null);
		
		if (isChecked) {
			nameText.setText(name);
//			ImgConfig.showHeadImg(name, headImg);
		} else {
//			headImg.setImageBitmap(locationImage(R.drawable.default_icon));
		}
		

		if (getIntent().getBooleanExtra("isRelogin", false)) {
			Tool.initToast(getApplicationContext(), "此账号已在别处登录");
		}
		else {
			if (name!=null && pwd!=null) {  //都不为空,自动登录
				loginAccount(name, pwd);
			}
		}
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			name = nameText.getText().toString();
			pwd = pwdText.getText().toString();
			if (TextUtils.isEmpty(name)) {
				Tool.initToast(LoginActivity.this, getString(R.string.register_name));
			} else if (TextUtils.isEmpty(pwd)) {
				Tool.initToast(LoginActivity.this, getString(R.string.register_password));
			} else {
				loginAccount(name, pwd);
			}
			break;
			
		case R.id.registBtn:
			Intent intent = new Intent(LoginActivity.this, RegActivity.class);
			startActivity(intent);
			break;

//		case R.id.changePwdBtn:
//			startActivity(new Intent(LoginActivity.this, ForgetPwdActivity.class));
//			break;
			
		default:
			break;
		}
	}
	
	private void loginAccount(final String userName, final String password) {
		new XmppLoadThread(this) {

			@Override
			protected Object load() {
				boolean isSuccess = XmppConnection.getInstance().login(userName, password);
				if (isSuccess) {
					Constants.USER_NAME = name;
					Constants.PWD = password;
					//用户详情
					Constants.loginUser = new User(XmppConnection.getInstance().getUserInfo(null));
//					Map<String, String> map = new HashMap<String, String>();
//					map.put("userName", Constants.USER_NAME);
//					String result = XmppConnection.getInstance().requestService(Constants.GET_USERINFO_DETAIL,map);
//					try {
//						JSONObject jsonObject = new JSONObject(result);
//						if (jsonObject.getString("state").equals("0")) {
//							Constants.loginUser = JsonUtil.jsonToObject(jsonObject.getString("items"), User.class);
//						}
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
				}
				return isSuccess;
			}

			@Override
			protected void result(Object o) {
				boolean isSuccess = (Boolean) o;
				if (isSuccess) {
					if (isChecked) {
						MyAndroidUtil.editXmlByString(Constants.LOGIN_ACCOUNT, name);
						MyAndroidUtil.editXmlByString(Constants.LOGIN_PWD, pwd);
					}
					else {
						MyAndroidUtil.removeXml(Constants.LOGIN_ACCOUNT);
						MyAndroidUtil.removeXml(Constants.LOGIN_PWD);
					}
					
//					if (Constants.USER_HEAD != null) {
//						MyAndroidUtil.editXmlByString(Constants.LOGIN_IMAGE,
//						FormatTools.getInstance().bitmaptoString(Constants.USER_HEAD, 100));
//					}
					
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				} else {
					Tool.initToast(LoginActivity.this, getResources().getString(R.string.login_error));
				}
			}

		};

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { 
			if (getIntent().getBooleanExtra("isRelogin", false)){
				return false;
			}
        }
		return true;
	}
	
	
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		checkBox.setChecked(isChecked);
		this.isChecked = isChecked;
		MyAndroidUtil.editXml(Constants.LOGIN_CHECK, isChecked);
		if (isChecked) {
			checkBox.setButtonDrawable(R.drawable.login_checked);
		} else {
			checkBox.setButtonDrawable(R.drawable.login_check);
		}
	}

	@Override
	public void afterTextChanged(Editable arg0) {
//		if (arg0.toString().equals(MyApplication.sharedPreferences.getString(Constants.LOGIN_ACCOUNT, null))) {
//			String head = MyApplication.sharedPreferences.getString(Constants.LOGIN_IMAGE, null);
//			if (head!=null) {
//				headImg.setImageBitmap(FormatTools.getInstance().byteToBitmap(head));
//			}
//		}else {
//			headImg.setImageBitmap(locationImage(R.drawable.default_icon));
//		}
//		ImgConfig.showHeadImg(arg0.toString(), headImg);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		
	}

}
