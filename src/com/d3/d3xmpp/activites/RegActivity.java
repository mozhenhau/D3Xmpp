/**
 * 
 */
package com.d3.d3xmpp.activites;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.packet.VCard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.model.User;
import com.d3.d3xmpp.util.MyAndroidUtil;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.util.Util;
import com.d3.d3xmpp.util.XmppLoadThread;
import com.d3.d3xmpp.xmpp.XmppConnection;

/**
 * @author MZH
 *
 */
public class RegActivity extends BaseActivity {
	@D3View TextView nameText,pwdText,pwdText1,emailText;//,emailText,phoneText
	@D3View(click="onClick") Button registBtn;
	String name,pwd;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_register);
		initTitle();
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.registBtn:
			name = nameText.getText().toString();
			pwd = pwdText.getText().toString();
			String againPwd = pwdText1.getText().toString();
			if (TextUtils.isEmpty(name)) {
				Tool.initToast(RegActivity.this, getString(R.string.register_name));
			}
			else if (TextUtils.isEmpty(pwd)) {
				Tool.initToast(RegActivity.this, getString(R.string.register_password));
			} else if (TextUtils.isEmpty(againPwd)) {
				Tool.initToast(RegActivity.this, getString(R.string.register_again_password));
			} else if (!againPwd.equals(pwd)) {
				Tool.initToast(RegActivity.this, getString(R.string.register_password_defferent));
			} else if (emailText.equals("") || !Util.getInstance().isEmail(emailText.getText().toString())) {
				Tool.initToast(RegActivity.this, getString(R.string.register_email_error));
			}
			else {
				createAccount(name, pwd);
			}
			break;

		default:
			break;
		}
	}
	
	
	private void createAccount(final String userName, final String passWord) {
		new XmppLoadThread(this) {

			@Override
			protected Object load() {
				IQ result = null;
				try {
					result = XmppConnection.getInstance().regist(userName, passWord);
					if (result!=null && result.getType() == IQ.Type.RESULT) {
						XmppConnection.getInstance().closeConnection();
						//ÕÍ…∆–≈œ¢
						Constants.loginUser = new User();
						Constants.loginUser.username = name;
						
						VCard vcard = new VCard();
						vcard.setField("email", emailText.getText().toString());
						XmppConnection.getInstance().login(name, pwd);
						Constants.loginUser = new User(XmppConnection.getInstance().getUserInfo(null));
						Constants.loginUser.email = emailText.getText().toString();
						XmppConnection.getInstance().changeVcard(vcard);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}

			@Override
			protected void result(Object object) {
				IQ result = (IQ) object;
				if (result == null) {
					Tool.initToast(getApplicationContext(), getString(R.string.service_result));
				} 
				else if (result.getType() == IQ.Type.ERROR) {
					if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
						Tool.initToast(getApplicationContext(), getString(R.string.accounted));
					} else {
						Tool.initToast(getApplicationContext(), getString(R.string.register_fail));
					}
				} else if (result.getType() == IQ.Type.RESULT) {
					Tool.initToast(getApplicationContext(), getString(R.string.register_success));
					Constants.USER_NAME = name;
					Constants.PWD = pwd;
					MyAndroidUtil.editXmlByString(Constants.LOGIN_ACCOUNT, name);
					MyAndroidUtil.editXmlByString(Constants.LOGIN_PWD, pwd);
					MyAndroidUtil.editXml(Constants.LOGIN_CHECK, true);
					
					Intent intent = new Intent(RegActivity.this, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent); 
					finish();
				}
			}

		};

	}
}
