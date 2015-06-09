/**
 * 
 */
package com.d3.d3xmpp.activites;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.adapter.SearchAdapter;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.util.XmppLoadThread;
import com.d3.d3xmpp.xmpp.XmppConnection;

/**
 * @author MZH
 *
 */
public class SearchActivity extends BaseActivity {
	@D3View ListView listView;
	@D3View EditText searchText;
	@D3View Button searchBtn;
	private SearchAdapter adapter;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_search);
		initTitle();
		adapter = new SearchAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getApplicationContext(), FriendActivity.class);
				intent.putExtra("username", adapter.getItem(position));
				startActivity(intent);
			}
		});
	}
	
	public void search(View v) {
		final String name = searchText.getText().toString();
		if (TextUtils.isEmpty(name)) {
			Tool.initToast(SearchActivity.this, getString(R.string.hint_search_name));
		} else {
			new XmppLoadThread(this) {
				
				@Override
				protected void result(Object object) {
					@SuppressWarnings("unchecked")
					List<String> userList = (ArrayList<String>)object;
					adapter.clear();
					adapter.addAll(userList);
					if (adapter.getCount()==0) {
						listView.setVisibility(View.GONE);
					}
					else {
						listView.setVisibility(View.VISIBLE);
					}
				}
				
				@Override
				protected Object load() {
					return XmppConnection.getInstance().searchUser(name);
				}
			};
		}
	}
}
