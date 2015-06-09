/**
 * 
 */
package com.d3.d3xmpp.activites;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.util.LoadThread;
import com.d3.d3xmpp.util.MyAndroidUtil;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.util.XmppLoadThread;
import com.d3.d3xmpp.xmpp.XmppConnection;
import com.d3.d3xmpp.R;

/**
 * @author MZH
 *
 */
public class ChangePwdActivity extends BaseActivity {
	@D3View EditText oldPwdView,pwdView,pwdView1;
	@D3View(click="onClick") Button subBtn;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_change_pwd);
		initTitle();
	}
	
	
	public void onClick(View v){
		if (oldPwdView.getText().toString().equals("")) {
			Tool.initToast(getApplicationContext(), "请输入旧密码");
		}
		else if(pwdView.getText().toString().equals("")){
			Tool.initToast(getApplicationContext(), "请输入新密码");
		}
		else if(pwdView1.getText().toString().equals("")){
			Tool.initToast(getApplicationContext(), "请确认新密码");
		}
		else if(!pwdView.getText().toString().equals(pwdView1.getText().toString())){
			Tool.initToast(getApplicationContext(), "两次密码不一致");
		}
		else if (!oldPwdView.getText().toString().equals(MyApplication.sharedPreferences.getString(Constants.LOGIN_PWD, null))) {
			Tool.initToast(getApplicationContext(), "原密码错误");
		}
		else
		{
			new XmppLoadThread(this) {
				
				@Override
				protected void result(Object object) {
					if ((Boolean) object) {
						Tool.initToast(getApplicationContext(), "修改成功");
						MyAndroidUtil.editXmlByString(Constants.LOGIN_PWD, pwdView.getEditableText().toString());
						finish();
					}
					else {
						Tool.initToast(getApplicationContext(), "修改失败");
					}
				}
				
				@Override
				protected Object load() {
					return XmppConnection.getInstance().changPwd(pwdView.getEditableText().toString());
				}
			};
//			
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("userName", Constants.USER_NAME);
//			map.put("originalPwd", oldPwdView.getEditableText().toString());
//			map.put("newPwd", pwdView.getEditableText().toString());
//			new LoadThread(this,Constants.UPDATE_PWD,map) {
//				
//				@Override
//				protected void refreshUI(String result) {
//					try {
//						JSONObject jsonObject = new JSONObject(result);
//						if (jsonObject.getString("state").equals("0")) {
//							Tool.initToast(getApplicationContext(), "修改成功");
//							MyAndroidUtil.editXmlByString(Constants.LOGIN_PWD, pwdView.getEditableText().toString());
//							finish();
//						}
//						else{
//							Tool.initToast(getApplicationContext(), "修改失败");
//						}
//						
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//			};
		}
		
	}
}
