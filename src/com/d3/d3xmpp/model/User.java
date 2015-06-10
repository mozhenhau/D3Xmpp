/**
 * 
 */
package com.d3.d3xmpp.model;

import org.jivesoftware.smackx.packet.VCard;

import com.d3.d3xmpp.util.ImageUtil;
import com.d3.d3xmpp.xmpp.XmppConnection;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * @author MZH
 *
 */
public class User {
	public String nickname;
	public String username;
	public String truename;
	public String email;
	public String headimg;
	public String intro;
	public String mobile;
	public String sex;
	public String adr;
	public VCard vCard;
	public Bitmap bitmap;
	public double lat = 0.0;
	public double lon = 0.0;
	
	public User() {
		super();
	}
	
	public User(VCard vCard){
		if (vCard!=null) {
			nickname = vCard.getField("nickName");
			email = vCard.getField("email");
			intro = vCard.getField("intro");
			sex = vCard.getField("sex");
			mobile = vCard.getField("mobile");
			adr = vCard.getField("adr");
			String latAndlon = vCard.getField("latAndlon");
			if (latAndlon!=null && !latAndlon.equals("")) {
				String[] latAndLons = latAndlon.split(",");
				lat = Double.valueOf(latAndLons[0]);
				lon = Double.valueOf(latAndLons[1]);
			}
			this.vCard = vCard;
			bitmap = ImageUtil.getBitmapFromBase64String(vCard.getField("avatar"));
		}
	}
	
	
	public void showHead(ImageView imageView) {
		if (bitmap!=null) {
			imageView.setImageBitmap(bitmap);
		}
	}
}
