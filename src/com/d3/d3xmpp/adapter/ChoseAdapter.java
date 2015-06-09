package com.d3.d3xmpp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.d3.d3xmpp.activites.FriendActivity;
import com.d3.d3xmpp.constant.ImgConfig;
import com.d3.d3xmpp.model.Friend;
import com.d3.d3xmpp.util.CircularImage;
import com.d3.d3xmpp.util.PinyinUtils;
import com.d3.d3xmpp.R;

public class ChoseAdapter extends ArrayAdapter<Friend> {
	Context context;
	public ChoseAdapter(Context context) {
		super(context, 0);
		this.context = context;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.row_contact, null);
		}
		final Friend item = getItem(position);
		LinearLayout cateLayout = (LinearLayout) convertView.findViewById(R.id.cateLayout);
		final CircularImage headImg = (CircularImage) convertView.findViewById(R.id.headImg);
		TextView nickView = (TextView) convertView.findViewById(R.id.nickView);
		TextView cateView = (TextView)convertView.findViewById(R.id.cateView);
		ImageView selectBtn = (ImageView)convertView.findViewById(R.id.selectBtn);
		selectBtn.setVisibility(View.VISIBLE);
		nickView.setText(item.username);
		
		if (item.isChose) {
			selectBtn.setImageResource(R.drawable.login_checked);
		}
		else {
			selectBtn.setImageResource(R.drawable.login_check);
		}
		ImgConfig.showHeadImg(item.username, headImg);
		
		headImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, FriendActivity.class);
				intent.putExtra("username", item.username);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		});
		
		
		//×ÖÄ¸ÅÅÁÐ
		String catalog = PinyinUtils.getPingYin(item.username).substring(0, 1).toUpperCase();
		
		if(position == 0){
			cateLayout.setVisibility(View.VISIBLE);
			cateView.setText(catalog);
		}
		else{
			String lastCatalog = PinyinUtils.getPingYin(
					getItem(position-1).username).substring(0,
					1);
			if (catalog.equalsIgnoreCase(lastCatalog)) {
				cateLayout.setVisibility(View.GONE);
			} else {
				cateLayout.setVisibility(View.VISIBLE);
				cateView.setText(catalog);
			}
		}
		return convertView;
	}

}