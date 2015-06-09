/**
 * 
 */
package com.d3.d3xmpp.d3View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.d3.d3xmpp.R;

/**
 * @author MZH
 *
 */
public class MoreFooter extends LinearLayout{
	private TextView moreData;
	private RelativeLayout loadingMore;
	/**
	 * @param context
	 */
	public MoreFooter(Context context) {
		super(context);
		init(context);
	}

	public void init(Context context){
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.morefooter, this);

		moreData = (TextView) findViewById(R.id.moredata);
		loadingMore = (RelativeLayout)findViewById(R.id.moreloadlayout);
	}
	
	public void setBackgroundColor(String color){
		moreData.setBackgroundColor(android.graphics.Color.parseColor(color));
		loadingMore.setBackgroundColor(android.graphics.Color.parseColor(color));
	}
	
	public void setOnClickListener(OnClickListener onClickListener){
		moreData.setOnClickListener(onClickListener);
	}
	
	public void setNormal(){
		setClickable(true);
		moreData.setVisibility(View.VISIBLE);
		loadingMore.setVisibility(View.GONE);
	}
	
	public void setDisable(){
		setClickable(false);
		moreData.setVisibility(View.GONE);
		loadingMore.setVisibility(View.VISIBLE);
	}
	
}
