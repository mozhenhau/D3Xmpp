package com.d3.d3xmpp.d3View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author MZH
 * 可选方案
 *@D3View TextView textView  成员变量命名对应xml的id名，不需要事件，直接留空
 *@D3View(id="textView") TextView textView;  //和xml的id不对应，自定义ID
 *@D3View(click="onClick") TextView textView;    //click="onClick" 对应在activity里有个公共方法onClick,会直接调用
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) 
public @interface D3View {
	public int id() default 0;
	public String click() default "";
	public String longClick() default "";
	public String itemClick() default "";
	public String itemLongClick() default "";
	public String focusChange() default "";
}