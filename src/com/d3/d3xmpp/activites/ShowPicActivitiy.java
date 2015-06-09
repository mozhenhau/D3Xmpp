package com.d3.d3xmpp.activites;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.d3.d3xmpp.R;

public class ShowPicActivitiy extends Activity{
    private LinearLayout ll_viewArea;
    private LinearLayout.LayoutParams parm;
    private ViewArea viewArea;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除title    
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
         //去掉Activity上面的状态栏  
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN,  
                       WindowManager.LayoutParams. FLAG_FULLSCREEN);
        
        setContentView(R.layout.acti_show_pic);
        
        ll_viewArea = (LinearLayout) findViewById(R.id.ll_viewArea);
        parm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
//        parm.gravity =  Gravity.CENTER;
        viewArea = new ViewArea(ShowPicActivitiy.this,getIntent().getStringExtra("picPath"));    //自定义布局控件，用来初始化并存放自定义imageView
        ll_viewArea.addView(viewArea,parm);

        ll_viewArea.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        
    }
}
//这段代码中要注意的问题是去掉title和状态栏两句代码必须放到 setContentView(R.layout.main);话的前面。而且这两句话必须有，因为后面计算回弹距离是根据全屏计算的（我的i9000就是480x800），如果不去掉title和状态栏，后面的回弹会有误差，总是回弹不到想要的位置。

//下面看看ViewArea.java文件。就是用来存放和初始化自定义imageView的地方。将来的自定义ImageView被限制在其内部移动缩放。
class ViewArea extends FrameLayout{  //前面说了ViewArea是一个布局， 所以这里当然要继承一个布局了。LinearLayout也可以
    private int imgDisplayW;    
    private int imgDisplayH;    
//    private int imgW;        
//    private int imgH;        
    private TouchView touchView;
    private DisplayMetrics dm;
    //resId为图片资源id
    public ViewArea(Context context,String img) { //第二个参数是图片的资源ID，当然也可以用别的方式获取图片
/*        dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        imgDisplayW = dm.widthPixels;
        imgDisplayH = dm.heightPixels;*/ //这种方式获取的屏幕大小和下面的方式结果是一样的，都是480x800（i9000分辨率）
        super(context);
        imgDisplayW = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();//这里的宽高要和xml中的LinearLayout大小一致，如果要指定大小。xml中 LinearLayout的宽高一定要用px像素单位，因为这里的宽高是像素，用dp会有误差！
        imgDisplayH = ((Activity)context).getWindowManager().getDefaultDisplay().getHeight();
        
        touchView = new TouchView(context,imgDisplayW,imgDisplayH);//这句就是我们的自定义ImageView
//        touchView.setImageResource(resId);//给我们的自定义imageView设置要显示的图片

//		ImgConfig.showImg(img, touchView);
        
        Bitmap bitmap = BitmapFactory.decodeFile(img);
        touchView.setImageBitmap(bitmap);
//        imgW = img.getWidth();
//        imgH = img.getHeight();
        //图片第一次加载进来，判断图片大小从而确定第一次图片的显示方式。
//        int layout_w = imgW>imgDisplayW?imgDisplayW:imgW;  
//        int layout_h = imgH>imgDisplayH?imgDisplayH:imgH;
        int layout_w = imgDisplayW;
        int layout_h = imgDisplayH;
        
//下面的代码是判断图片初始显示样式的，当然可以根据你的想法随意显示，我这里是将宽大于高的图片按照宽缩小的比例把高压缩，前提必须是宽度超出了屏幕大小，相反，如果高大于宽，我将图片按照高缩小的比例把宽压缩，前提必须是高度超出了屏幕大小
//        if(imgW>=imgH) {
//                layout_h = (int) (imgH*((float)imgDisplayW/imgW));
//        }else {
//                layout_w = (int) (imgW*((float)imgDisplayH/imgH));
//        }
//这里需要注意的是，采用FreamLayout或者LinearLayout的好处是，如果压缩后的图片扔有一个边大于屏幕，那么只显示在屏幕内的部分，可以通过移动后看见外部（不会裁剪掉图片），如果采用RelativeLayout布局，图片会始终完整显示在屏幕内部，不会有超出屏幕的现象。如果图片不是完全占满屏幕，那么在屏幕上没有图片的地方拖动，图片也会移动，这样的体验不太好，建议用FreamLayout或者LinearLayout。
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(layout_w,layout_h);
        params.gravity =  Gravity.CENTER;
        touchView.setLayoutParams(params);//这是自定义imageView的大小，也就是触摸范围
        this.addView(touchView);
    }

}

class TouchView extends ImageView
{
    static final int NONE = 0;//表示当前没有状态
    static final int DRAG = 1;     //表示当前处于移动状态
    static final int ZOOM = 2;     //表示当前处于缩放状态
    static final int BIGGER = 3;   //表示放大图片
    static final int SMALLER = 4;  //表示缩小图片
    private int mode = NONE;      //mode用于标示当前处于什么状态

    private float beforeLenght;  //第一次触摸两点的距离
    private float afterLenght;    //移动后两点的距离
    private float scale = 0.04f;  //缩放因子
   
    private int screenW;//下面两句图片的移动范围，及ViewArea的范围，也就是linearLayout的范围，也就是屏幕方位（都是填满父控件属性）
    private int screenH;
    
    
    private int start_x;//开始触摸点
    private int start_y;
    private int stop_x ;//结束触摸点
    private int stop_y ;
    private TranslateAnimation trans; //回弹动画
    
    public TouchView(Context context,int w,int h)//这里传进来的w，h就是图片的移动范围
    {
        super(context);
        this.setPadding(0, 0, 0, 0);
        screenW = w;
        screenH = h;
    }
    //用来计算2个触摸点的距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
    

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {    
        switch (event.getAction() & MotionEvent.ACTION_MASK) {  //MotionEvent.ACTION_MASK 表示多点触控事件
        case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                stop_x = (int) event.getRawX();//表示相对于屏幕左上角为原点的坐标
                stop_y = (int) event.getRawY();//同上
                start_x = stop_x - this.getLeft();//用(int) event.getX();一样,表示相对于当前点击Widget（控件）左上角的坐标，这里就是相对于自定义imageView左上角的坐标.建议用前者，如果不是全屏拖动，而是指定范围内，一样适用！
                start_y = stop_y - this.getTop();////用(int) event.getY();一样,this.getTop()表示其顶部相对于父控件的距离
                
                if(event.getPointerCount()==2)
                    beforeLenght = spacing(event);
                break;
        case MotionEvent.ACTION_POINTER_DOWN:
                if (spacing(event) > 10f) {
                        mode = ZOOM;
                        beforeLenght = spacing(event);
                }
                break;
        case MotionEvent.ACTION_UP:

                int disX = 0;
                int disY = 0;
                if(getHeight()<=screenH )//
                {
                    if(this.getTop()<0 )
                    {
                        disY = getTop();
//layout(left , top, right,bottom)函数表示设置view的位置。
                        this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());

                    }
                    else if(this.getBottom()>=screenH)
                    {
                        disY = getHeight()- screenH+getTop();
                        this.layout(this.getLeft(), screenH-getHeight(), this.getRight(), screenH);
                    }
                }else{
                    int Y1 = getTop();
                    int Y2 = getHeight()- screenH+getTop();
                        if(Y1>0)
                        {
                            disY= Y1;
                            this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
                        }else if(Y2<0){
                            disY = Y2;
                            this.layout(this.getLeft(), screenH-getHeight(), this.getRight(), screenH);
                        }
                }
                if(getWidth()<=screenW)
                {
                    if(this.getLeft()<0)
                    {
                        disX = getLeft();
                        this.layout(0, this.getTop(), 0+getWidth(), this.getBottom());
                    }
                    else if(this.getRight()>screenW)
                    {
                        disX = getWidth()-screenW+getLeft();
                        this.layout(screenW-getWidth(), this.getTop(), screenW, this.getBottom());
                    }
                }else {
                    int X1 = getLeft();
                    int X2 = getWidth()-screenW+getLeft();
                    if(X1>0) {
                        disX = X1;
                        this.layout(0, this.getTop(), 0+getWidth(), this.getBottom());
                    }else if(X2<0) {
                        disX = X2;
                        this.layout(screenW-getWidth(), this.getTop(), screenW, this.getBottom());
                    }
                    
                }
                //如果图片缩放到宽高任意一个小于100，那么自动放大，直到大于100.
                while(getHeight()<100||getWidth()<100) {
                    
                    setScale(scale,BIGGER);
                }
//根据disX和disY的偏移量采用移动动画回弹归位，动画时间为500毫秒。
                if(disX!=0 || disY!=0)
                {
                    trans = new TranslateAnimation(disX, 0, disY, 0);
                    trans.setDuration(500);
                    this.startAnimation(trans);
                }
                mode = NONE;
                break;
        case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
//执行拖动事件的时，不断变换自定义imageView的位置从而达到拖动效果
                        this.setPosition(stop_x - start_x, stop_y - start_y, stop_x + this.getWidth() - start_x, stop_y - start_y + this.getHeight());               
                        stop_x = (int) event.getRawX();
                        stop_y = (int) event.getRawY();
                        
                } else if (mode == ZOOM) {
                    if(spacing(event)>10f)
                    {
                        afterLenght = spacing(event);
                        float gapLenght = afterLenght - beforeLenght;                     
                        if(gapLenght == 0) {  
                           break;
                        }
//图片宽度（也就是自定义imageView）必须大于70才可以缩放
                        else if(Math.abs(gapLenght)>5f&&getWidth()>70)
                        {
                            if(gapLenght>0) { 
                                this.setScale(scale,BIGGER);   
                            }else {  
                                this.setScale(scale,SMALLER);   
                            }                             
                            beforeLenght = afterLenght; //这句不能少。
                        }
                    }
                }
                break;
        }
        return true;    
    }
    

    private void setScale(float temp,int flag) {   
        
        if(flag==BIGGER) {  
//setFrame(left , top, right,bottom)函数表示改变当前view的框架，也就是大小。
            this.setFrame(this.getLeft()-(int)(temp*this.getWidth()),    
                          this.getTop()-(int)(temp*this.getHeight()),    
                          this.getRight()+(int)(temp*this.getWidth()),    
                          this.getBottom()+(int)(temp*this.getHeight()));      
        }else if(flag==SMALLER){   
            this.setFrame(this.getLeft()+(int)(temp*this.getWidth()),    
                          this.getTop()+(int)(temp*this.getHeight()),    
                          this.getRight()-(int)(temp*this.getWidth()),    
                          this.getBottom()-(int)(temp*this.getHeight()));   
        }   
    }
    

    private void setPosition(int left,int top,int right,int bottom) {  
        this.layout(left,top,right,bottom);               
    }

}