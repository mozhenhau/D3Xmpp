///**
// * 
// */
//package com.d3.d3xmpp.activites;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import com.d3.d3xmpp.constant.Constants;
//import com.d3.d3xmpp.d3View.D3View;
//import com.d3.d3xmpp.util.LoadThread;
//import com.d3.d3xmpp.util.Tool;
//import com.d3.d3xmpp.R;
//
///**
// * @author MZH
// *
// */
//public class ForgetPwdActivity extends BaseActivity {
//	@D3View EditText nameText,emailText;
//	@D3View(click="onClick  ") Button subBtn;
//	
//	@Override
//	protected void onCreate(Bundle arg0) {
//		super.onCreate(arg0);
//		setContentView(R.layout.acti_forget_pwd);
//		initTitle();
//	}
//	
//	
//	public void onClick(View v){
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("email", emailText.getText().toString());
//		map.put("userName", nameText.getText().toString());
//		new LoadThread(ForgetPwdActivity.this,Constants.FORGET_PWD,map) {
//			
//			@Override
//			protected void refreshUI(String result) {
//				try {
//					JSONObject jsonObject = new JSONObject(result);
//					if (jsonObject.getString("state").equals("0")) {
//						Tool.initToast(getApplicationContext(), "邮件已发至您的注册邮箱");
//						finish();
//					}
//					else if(jsonObject.getString("state").equals("2")){
//						Tool.initToast(getApplicationContext(), "无预留邮箱");
//					}
//					else if(jsonObject.getString("state").equals("3")){
//						Tool.initToast(getApplicationContext(), "预留邮箱不一致");
//					}
//					else if(jsonObject.getString("state").equals("4")){
//						Tool.initToast(getApplicationContext(), "邮箱格式不正确");
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		
//	}
//}
