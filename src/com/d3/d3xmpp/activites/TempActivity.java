/**
 * 
 */
package com.d3.d3xmpp.activites;

import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.R;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * @author MZH
 *
 */
public class TempActivity extends BaseActivity {
	@D3View TextView test;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_register);
		initTitle();
	}
	
	
	public void onClick(View v){
		
		
	}
}
