/**
 * 
 */
package com.d3.d3xmpp.fragments;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.activites.ChangePwdActivity;
import com.d3.d3xmpp.activites.CropImageActivity;
import com.d3.d3xmpp.activites.LoginActivity;
import com.d3.d3xmpp.activites.PicSrcPickerActivity;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.d3View.D3Fragment;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.util.CircularImage;
import com.d3.d3xmpp.util.MyAndroidUtil;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.util.Util;
import com.d3.d3xmpp.util.XmppLoadThread;
import com.d3.d3xmpp.util.wheel.OnWheelChangedListener;
import com.d3.d3xmpp.util.wheel.WheelView;
import com.d3.d3xmpp.util.wheel.adapters.ArrayWheelAdapter;
import com.d3.d3xmpp.util.wheel.model.CityModel;
import com.d3.d3xmpp.util.wheel.model.DistrictModel;
import com.d3.d3xmpp.util.wheel.model.ProvinceModel;
import com.d3.d3xmpp.util.wheel.service.XmlParserHandler;
import com.d3.d3xmpp.xmpp.XmppConnection;

/**
 * @author MZH
 *
 */
public class MeFragment extends D3Fragment implements OnWheelChangedListener{
	@D3View(click="onClick")LinearLayout headLayout,nickLayout,nameLayout,emailLayout,phoneLayout,pwdLayout,sexLayout,signLayout,adrLayout,changeLayout,changeAdrLayout;
	@D3View TextView usernameView,nameView,emailView,phoneView,sexView,signView,changeNameView,adrView; //nickView,
	@D3View ScrollView scrollView;
	@D3View EditText changeText;
	@D3View RadioGroup sexGroup;
	@D3View RadioButton manRadio,womanRadio;
	@D3View CheckBox shakeBtn, soundBtn,shareBtn;
	@D3View(click="onClick") CircularImage headView;
	@D3View(click="onClick")Button exitBtn,subBtn,sureBtn,cancelBtn;
	private String field;
	private TextView changeView;
	private WheelView mViewProvince,mViewCity,mViewDistrict;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = setContentView(inflater, R.layout.acti_me);
		mViewProvince = (WheelView) view.findViewById(R.id.id_province);
		mViewCity = (WheelView) view.findViewById(R.id.id_city);
		mViewDistrict = (WheelView) view.findViewById(R.id.id_district);
		mViewProvince.addChangingListener(this);
    	mViewCity.addChangingListener(this);
    	mViewDistrict.addChangingListener(this);
    	setUpData();
//		if (Constants.USER_NAME!=null) {
//			ImgConfig.showHeadImg(Constants.USER_NAME,headView);
//		}
		if (Constants.loginUser!=null) {
			if (Constants.USER_NAME!=null) {
				usernameView.setText(Constants.USER_NAME);
			}
			if (Constants.loginUser.nickname!=null) {
				nameView.setText(Constants.loginUser.nickname);
			}
			if (Constants.loginUser.email!=null) {
				emailView.setText(Constants.loginUser.email);
			}
			if (Constants.loginUser.mobile!=null) {
				phoneView.setText(Constants.loginUser.mobile);
			}
			if (Constants.loginUser.sex!=null) {
				sexView.setText(Constants.loginUser.sex);
			}
			if (Constants.loginUser.intro!=null) {
				signView.setText(Constants.loginUser.intro);
			}
			if (Constants.loginUser.bitmap!=null) {
				headView.setImageBitmap(Constants.loginUser.bitmap);
			}
			if (Constants.loginUser.adr!=null) {
				adrView.setText(Constants.loginUser.adr);
			}
		}
		sexGroup.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == manRadio.getId()) {
					changeText.setText("男");
				}
				else {
					changeText.setText("女");
				}
			}
		});
		shakeBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					MyAndroidUtil.editXml("isShake", true);
				} else {
					MyAndroidUtil.editXml("isShake", false);
				}
			}
		});

		soundBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					MyAndroidUtil.editXml("isSound", true);
				} else {
					MyAndroidUtil.editXml("isSound", false);
				}
			}
		});
		
		shareBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					MyAndroidUtil.editXml("isShare", true);
					MyApplication.getInstance().uploadAdr();
				} else {
					MyAndroidUtil.editXml("isShare", false);
					MyApplication.getInstance().clearAdr();
				}
			}
		});
		
		shareBtn.setChecked(MyApplication.sharedPreferences.getBoolean("isShare", true));
		shakeBtn.setChecked(MyApplication.sharedPreferences.getBoolean("isShake", true));
		soundBtn.setChecked(MyApplication.sharedPreferences.getBoolean("isSound", true));
		return view;
	}
	
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sureBtn:
			showSelectedResult();
			break;
		
		case R.id.exitBtn:
			MyAndroidUtil.removeXml(Constants.LOGIN_PWD);
			if (!MyApplication.sharedPreferences.getBoolean(Constants.LOGIN_CHECK, false)) {
				MyAndroidUtil.removeXml(Constants.LOGIN_ACCOUNT);
			}
			Constants.USER_NAME = "";
			Constants.loginUser = null;
			XmppConnection.getInstance().getFriendList().clear();
			XmppConnection.getInstance().myRooms.clear();
			XmppConnection.getInstance().closeConnection();
			getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
			getActivity().finish();
			break;
			
		case R.id.subBtn:
			//提交修改
			String cText = changeText.getEditableText().toString();
			if (field.equals("mobile") && !Util.getInstance().isMobileNumber(cText)) {
				Tool.initToast(getActivity().getApplicationContext(), "不是手机号码");
			}
			else if(field.equals("email") && !Util.getInstance().isEmail(cText)){
				Tool.initToast(getActivity().getApplicationContext(), "不是邮箱");
			}
			else {
				changeView.setText(changeText.getEditableText().toString());
				Constants.loginUser.vCard.setField(field, changeText.getEditableText().toString());
				XmppConnection.getInstance().changeVcard(Constants.loginUser.vCard);
				changeLayout.setVisibility(View.GONE);
				changeText.setText("");
			}
			break;
			
		case R.id.cancelBtn:
			changeLayout.setVisibility(View.GONE);
			changeText.setText("");
			break;
		
		case R.id.headView:
			Intent intent = new Intent();
			CropImageActivity.isAutoSend = false;
			intent.setClass(getActivity(), PicSrcPickerActivity.class);
			getActivity().startActivityForResult(intent,PicSrcPickerActivity.CROP);
			break;
//		case R.id.nickLayout:
//			showChangelayout("修改昵称", "nickName", nickView);
//			break;
		case R.id.nameLayout:
			showChangelayout("修改昵称", "nickName", nameView);
			break;
		case R.id.emailLayout:
			showChangelayout("修改邮箱", "email", emailView);
			break;
		case R.id.phoneLayout:
			showChangelayout("修改手机号码", "mobile", phoneView);
			break;
		case R.id.pwdLayout:
			startActivity(new Intent(getActivity(), ChangePwdActivity.class));
			break;
		case R.id.sexLayout:
			showChangelayout("修改性别", "sex", sexView);
			break;
		case R.id.adrLayout:
//			showChangelayout("修改地区", "adr", adrView);
			changeAdrLayout.setVisibility(View.VISIBLE);
			
			break;
		case R.id.signLayout:
			showChangelayout("修改签名", "intro", signView);
			break;

		default:
			break;
		}
	}
	
	
	/**
	 * @param name  //显示修改XXX
	 * @param field    //修改的字段名
	 * @param fieldView  //修改的view
	 */
	public void showChangelayout(String name,String field,TextView fieldView) {
		changeLayout.setVisibility(View.VISIBLE);
		if (field.equals("sex")) {
			sexGroup.setVisibility(View.VISIBLE);
			changeText.setVisibility(View.INVISIBLE);
		}
		else {
			sexGroup.setVisibility(View.GONE);
			changeText.setVisibility(View.VISIBLE);
		}
		changeNameView.setText(name);
		this.field = field;
		this.changeView = fieldView;
		scrollView.fullScroll(ScrollView.FOCUS_UP);
	}
	
	
	public void changeHead(final String imgPath) {
		new XmppLoadThread(getActivity()) {
			
			@Override
			protected void result(Object object) {
				if (object!=null) {
					headView.setImageBitmap((Bitmap)object);
				}
			}
			
			@Override
			protected Object load() {
				return XmppConnection.getInstance().changeImage(new File(imgPath));
			}
		};
	}
	
	
	
	
	//以下是选地区用的。

	/**
	 * 所有省
	 */
	protected String[] mProvinceDatas;
	/**
	 * key - 省 value - 市
	 */
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 市 values - 区
	 */
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
	
	/**
	 * key - 区 values - 邮编
	 */
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>(); 

	/**
	 * 当前省的名称
	 */
	protected String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	protected String mCurrentCityName;
	/**
	 * 当前区的名称
	 */
	protected String mCurrentDistrictName ="";
	
	/**
	 * 当前区的邮政编码
	 */
	protected String mCurrentZipCode ="";
	
	/**
	 * 解析省市区的XML数据
	 */
	
    protected void initProvinceDatas()
	{
		List<ProvinceModel> provinceList = null;
    	AssetManager asset = getActivity().getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			provinceList = handler.getDataList();
			//*/ 初始化默认选中的省、市、区
			if (provinceList!= null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList!= null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
					List<DistrictModel> districtList = cityList.get(0).getDistrictList();
					mCurrentDistrictName = districtList.get(0).getName();
					mCurrentZipCode = districtList.get(0).getZipcode();
				}
			}
			//*/
			mProvinceDatas = new String[provinceList.size()];
        	for (int i=0; i< provinceList.size(); i++) {
        		// 遍历所有省的数据
        		mProvinceDatas[i] = provinceList.get(i).getName();
        		List<CityModel> cityList = provinceList.get(i).getCityList();
        		String[] cityNames = new String[cityList.size()];
        		for (int j=0; j< cityList.size(); j++) {
        			// 遍历省下面的所有市的数据
        			cityNames[j] = cityList.get(j).getName();
        			List<DistrictModel> districtList = cityList.get(j).getDistrictList();
        			String[] distrinctNameArray = new String[districtList.size()];
        			DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
        			for (int k=0; k<districtList.size(); k++) {
        				// 遍历市下面所有区/县的数据
        				DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
        				// 区/县对于的邮编，保存到mZipcodeDatasMap
        				mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
        				distrinctArray[k] = districtModel;
        				distrinctNameArray[k] = districtModel.getName();
        			}
        			// 市-区/县的数据，保存到mDistrictDatasMap
        			mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
        		}
        		// 省-市的数据，保存到mCitisDatasMap
        		mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
        	}
        } catch (Throwable e) {  
            e.printStackTrace();  
        } finally {
        	
        } 
	}

	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(), mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
		updateCities();
		updateAreas();
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[] { "" };
		}
		mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(), areas));
		mViewDistrict.setCurrentItem(0);
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(), cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	private void showSelectedResult() {
		Toast.makeText(getActivity(), "当前选中:"+mCurrentProviceName+","+mCurrentCityName+","
				+mCurrentDistrictName+","+mCurrentZipCode, Toast.LENGTH_SHORT).show();
		adrView.setText(mCurrentDistrictName);
		Constants.loginUser.vCard.setField("adr", mCurrentDistrictName);
		XmppConnection.getInstance().changeVcard(Constants.loginUser.vCard);
		changeAdrLayout.setVisibility(View.GONE);
	}
	
	@Override
	public void onResume() {
		changeText.clearFocus();
		super.onResume();
	}
}
