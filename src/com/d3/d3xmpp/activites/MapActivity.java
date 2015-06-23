/**
 * 
 */
package com.d3.d3xmpp.activites;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.d3.d3xmpp.R;
import com.d3.d3xmpp.d3View.D3View;

/**
 * @author MZH
 *
 */
public class MapActivity extends BaseActivity {
	@D3View(click="onClick") ImageView leftBtn;
	MapView mMapView = null;  
	BaiduMap mBaiduMap ;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.acti_map);
		double lat = getIntent().getDoubleExtra("lat", 0.0);
		double lon = getIntent().getDoubleExtra("lon", 0.0);
		
		mMapView = (MapView) findViewById(R.id.bmapView);  
		mBaiduMap =  mMapView.getMap();
		// 开启定位图层  
		mBaiduMap.setMyLocationEnabled(true);  
		// 构造定位数据  
		System.out.println("rttttttt,"+lat+","+lon);
		MyLocationData locData = new MyLocationData.Builder()  
		    .accuracy((float)40.0)  
		    // 此处设置开发者获取到的方向信息，顺时针0-360  
		    .direction(100).latitude(lat)  
		    .longitude(lon).build();  
		// 设置定位数据  
		mBaiduMap.setMyLocationData(locData);  
		LatLng ll = new LatLng(lat,lon);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
//		 当不需要定位图层时关闭定位图层  
	}
	
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		}
	}
	
	 @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
        }  
}
