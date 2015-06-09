package com.d3.d3xmpp.d3View;

import java.lang.reflect.Method;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * @author MZH
 *
 */
public class EventListener implements OnClickListener, OnLongClickListener, OnItemClickListener,OnItemLongClickListener ,OnFocusChangeListener{

	private Object handler;
	
	private String clickMethod;
	private String longClickMethod;
	private String itemClickMethod;
	private String itemLongClickMehtod;
	private String focusChangeMethod;
	
	public EventListener(Object handler) {
		this.handler = handler;
	}
	
	public EventListener click(String method){
		this.clickMethod = method;
		return this;
	}
	
	public EventListener longClick(String method){
		this.longClickMethod = method;
		return this;
	}
	
	public EventListener itemLongClick(String method){
		this.itemLongClickMehtod = method;
		return this;
	}
	
	public EventListener itemClick(String method){
		this.itemClickMethod = method;
		return this;
	}
	
	public EventListener focusChange(String method){
		this.focusChangeMethod = method;
		return this;
	}
	
	public boolean onLongClick(View v) {
		return invokeLongClickMethod(handler,longClickMethod,v);
	}
	
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		return invokeItemLongClickMethod(handler,itemLongClickMehtod,arg0,arg1,arg2,arg3);
	}
	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		invokeItemClickMethod(handler,itemClickMethod,arg0,arg1,arg2,arg3);
	}
	
	public void onClick(View v) {
		
		invokeClickMethod(handler, clickMethod, v);
	}
	
	public void onFocusChange(View v, boolean hasFocus) {
		invokeFocusChangeMethod(handler, focusChangeMethod,hasFocus,v);
	}
	
	
	private static Object invokeFocusChangeMethod(Object handler, String methodName, Object... params) {
		if(handler == null) return null;
		Method method = null;
		try{   
			method = handler.getClass().getDeclaredMethod(methodName,boolean.class,View.class);
			if(method!=null)
				return method.invoke(handler, params);	
			else
				throw new RuntimeException("no such method:"+methodName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

	private static Object invokeClickMethod(Object handler, String methodName,  Object... params){
		if(handler == null) return null;
		Method method = null;
		try{   
			method = handler.getClass().getDeclaredMethod(methodName,View.class);
			if(method!=null)
				return method.invoke(handler, params);	
			else
				throw new RuntimeException("no such method:"+methodName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	
	private static boolean invokeLongClickMethod(Object handler, String methodName,  Object... params){
		if(handler == null) return false;
		Method method = null;
		try{   
			//public boolean onLongClick(View v)
			method = handler.getClass().getDeclaredMethod(methodName,View.class);
			if(method!=null){
				Object obj = method.invoke(handler, params);
				return obj==null?false:Boolean.valueOf(obj.toString());	
			}
			else
				throw new RuntimeException("no such method:"+methodName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	
	
	private static Object invokeItemClickMethod(Object handler, String methodName,  Object... params){
		if(handler == null) return null;
		Method method = null;
		try{   
			///onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			method = handler.getClass().getDeclaredMethod(methodName,AdapterView.class,View.class,int.class,long.class);
			if(method!=null)
				return method.invoke(handler, params);	
			else
				throw new RuntimeException("no such method:"+methodName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	private static boolean invokeItemLongClickMethod(Object handler, String methodName,  Object... params){
		if(handler == null) throw new RuntimeException("invokeItemLongClickMethod: handler is null :");
		Method method = null;
		try{   
			///onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
			method = handler.getClass().getDeclaredMethod(methodName,AdapterView.class,View.class,int.class,long.class);
			if(method!=null){
				Object obj = method.invoke(handler, params);
				return Boolean.valueOf(obj==null?false:Boolean.valueOf(obj.toString()));	
			}
			else
				throw new RuntimeException("no such method:"+methodName);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
}
