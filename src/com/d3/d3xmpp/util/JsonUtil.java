package com.d3.d3xmpp.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;

public class JsonUtil
{
	public static Boolean validate(String json)
	{
//		Log.d("json", json);
		Boolean result = false;
		try
		{
			if(json !=null){
				JSONArray jsonArray = new JSONArray(json);
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				result = jsonObject.getBoolean("validate");
			}
			return result;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return true;
		}
	}
	
	public static <T> T jsonToObject(String json, Class<T> classOfT)
	{
		Gson gson = new Gson();
		T object = (T) gson.fromJson(json, classOfT);
		return object;
	}
	
	public static <T> T jsonToObject(String json, Type type)
	{
		Gson gson = new Gson();
		T list =  (T) gson.fromJson(json, type);
		return list;
	}
	
	
	public static <T> List<T> jsonToObjectList(String json, Class<T> classOfT)
	{
		List<T> list = new ArrayList<T>();
		if(json !=null && !"null".equals(json) && !"".equals(json)){
			Gson gson = new Gson();
			try
			{
				JSONArray jsonArray = new JSONArray(json);
				JSONObject jsonObject;
				 T object;
				for(int i = 0; i < jsonArray.length(); i++)
				{
					 jsonObject = (JSONObject) jsonArray.get(i);
					 object = (T) gson.fromJson(jsonObject.toString(), classOfT);
					 list.add(object);
					 object = null;
				}
			}
			catch (JSONException e)
			{
				Log.e("json", "json格式错误");
				e.printStackTrace();
			}
		}
		return list;
	}

	public static String objectToJson(Object object)
	{
		Gson gson = new Gson();
		String json = gson.toJson(object);
		return json;
	}

	public static HashMap<String, Object> objectToHashMap(Object object)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		String json = objectToJson(object);
		map = jsonToHashMap(json);
		return map;
	}
	
	public static HashMap<String, Object> jsonToHashMap(String json)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		try
		{
			JSONObject jsonObject = new JSONObject(json);
			Iterator<?> it = jsonObject.keys();
			String key;
			Object value;
			while (it.hasNext())
			{
				key = String.valueOf(it.next());
				value = jsonObject.get(key);
				map.put(key, value);
			}
		}
		catch (JSONException e)
		{
			Log.e("json", "json格式错误");
			e.printStackTrace();
		}
		return map;
	}

	public static void AddJsonObjectToHashMapList(String json, List<HashMap<String, Object>> list)
	{
//		Log.d("json", json);
		HashMap<String, Object> map = new HashMap<String, Object>();
		try
		{
			JSONArray jsonArray = new JSONArray(json);
			JSONObject jsonObject;
			//第一个用于验证
			for(int i = 1; i < jsonArray.length(); i++)
			{
				 jsonObject = (JSONObject) jsonArray.get(i);
				 map = jsonToHashMap(jsonObject.toString());
				 //分页查询时需要使用，防止动态更新造成的重复添加
				 if(!list.contains(map))
					 list.add(map);
			}
		}
		catch (JSONException e)
		{
			Log.e("json", "json格式错误");
			e.printStackTrace();
		}
	}
	
	public static List<HashMap<String, Object>> jsonToHashMapList(String json)
	{
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		AddJsonObjectToHashMapList(json, list);
		return list;
	}
}
