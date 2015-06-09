package com.d3.d3xmpp.d3View.gifView;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * GifView<br>
 * 本类可以显示一个gif动画，其使用方法和android的其它view（如imageview)一样。<br>
 * 如果要显示的gif太大，会出现OOM的问题。缓冲到tmp
 * @author liao
 *
 */
public class GifView extends ImageView implements GifAction{

        /**gif解码器*/
        private GifDecoder gifDecoder = null;
        /**当前要画的帧的图*/
        private Bitmap currentImage = null;
        
        private boolean isRun = true;
        
        private boolean pause = false;

        private DrawThread drawThread = null;
        
        private Context context = null;
        
        private boolean cacheImage = false;
        
        private View backView = null;
        
        private GifImageType animationType = GifImageType.SYNC_DECODER;

        /**
         * 解码过程中，Gif动画显示的方式<br>
         * 如果图片较大，那么解码过程会比较长，这个解码过程中，gif如何显示
         * @author liao
         *
         */
        public enum GifImageType{
                /**
                 * 在解码过程中，不显示图片，直到解码全部成功后，再显示
                 */
                WAIT_FINISH (0),
                /**
                 * 和解码过程同步，解码进行到哪里，图片显示到哪里
                 */
                SYNC_DECODER (1),
                /**
                 * 在解码过程中，只显示第一帧图片
                 */
                COVER (2);
                
                GifImageType(int i){
                        nativeInt = i;
                }
                final int nativeInt;
        }
        
        
        public GifView(Context context) {
        super(context);
        this.context = context;
        //gifDecoder = new GifDecoder(this);
        setScaleType(ImageView.ScaleType.FIT_XY);
    }
    
    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        
    }  
    
    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
       // TypedArray a = context.obtainStyledAttributes(attrs,R.array.);
        //gifDecoder = new GifDecoder(this);
        setScaleType(ImageView.ScaleType.FIT_XY);
    }
    
    /**
     * 设置图片，并开始解码
     * @param gif 要设置的图片
     */
    private void setGifDecoderImage(byte[] gif){

        if(gifDecoder == null){
            gifDecoder = new GifDecoder(this);
        }
        gifDecoder.setGifImage(gif);
        gifDecoder.start();
    }
    
    /**
     * 设置图片，开始解码
     * @param is 要设置的图片
     */
    private void setGifDecoderImage(InputStream is){

        if(gifDecoder == null){
            gifDecoder = new GifDecoder(this);
        }
        gifDecoder.setGifImage(is);
        gifDecoder.start();
        
        
    }
    
    /**
     * 把本Gif动画设置为另外view的背景
     * @param v 要使用gif作为背景的view
     */
    public void setAsBackground(View v){
        backView = v;
    }
    
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        if(gifDecoder != null)
                gifDecoder.free();
        
                return null;
        }
    
    /**
     * @hide
     * 设置缓存图片<br>
     * 如果缓存图片，每一Frame的间隔太快的话，会出现跳帧的现象<br>
     * 如果设置了缓存图片，则你必须调用destroy来作缓存图片的清理。
     */   
//    public void setCahceImage(){
//        if(gifDecoder == null){
//            gifDecoder = new GifDecoder(this);
//        }
//        cacheImage = true;
//        gifDecoder.setCacheImage(true, context);
//    }
    
    
    /**
     * 以字节数据形式设置gif图片
     * @param gif 图片
     */
    public void setGifImage(byte[] gif){
        setGifDecoderImage(gif);
    }
    
    /**
     * 以字节流形式设置gif图片
     * @param is 图片
     */
    public void setGifImage(InputStream is){
        setGifDecoderImage(is);
    }
    
    /**
     * 以资源形式设置gif图片
     * @param resId gif图片的资源ID
     */
    public void setGifImage(int resId){
		if (currentImage != null) {
			currentImage = null;
		}
    	if (gifDecoder != null) {
			stopDecodeThread();
			gifDecoder = null;
		}
        Resources r = getResources();
        InputStream is = r.openRawResource(resId);
        setGifDecoderImage(is);
    }
    
	
    
    
	/**
	 * 中断解码线程
	 */
	private void stopDecodeThread() {
		if (gifDecoder != null && gifDecoder.getState() != Thread.State.TERMINATED) {
			gifDecoder.interrupt();
			gifDecoder.destroy();
		}
	}
    
    public void destroy(){
        if(gifDecoder != null)
            gifDecoder.free();
    }
    
    /**
     * 只显示第一帧图片<br>
     * 调用本方法后，gif不会显示动画，只会显示gif的第一帧图
     */
    public void showCover(){
        if(gifDecoder == null)
                return;
        pause = true;
        currentImage = gifDecoder.getImage();
        invalidate();
    }
    
    /**
     * 继续显示动画<br>
     * 本方法在调用showCover后，会让动画继续显示，如果没有调用showCover方法，则没有任何效果
     */
    public void showAnimation(){
        if(pause){
                pause = false;
        }
    }
    
    /**
     * 设置gif在解码过程中的显示方式<br>
     * <strong>本方法只能在setGifImage方法之前设置，否则设置无效</strong>
     * @param type 显示方式
     */
    public void setGifImageType(GifImageType type){
        if(gifDecoder == null)
                animationType = type;
    }
  

    
    /**
     * @hide
     */
    public void parseOk(boolean parseStatus,int frameIndex){
        if(parseStatus){
                if(gifDecoder != null){
                        switch(animationType){
                        case WAIT_FINISH:
                                if(frameIndex == -1){
                                        if(gifDecoder.getFrameCount() > 1){     //当帧数大于1时，启动动画线程
                                        DrawThread dt = new DrawThread();
                                dt.start();
                                }else{
                                        reDraw();
                                }
                                }
                                break;
                        case COVER:
                                if(frameIndex == 1){
                                        currentImage = gifDecoder.getImage();
                                        reDraw();
                                }else if(frameIndex == -1){
                                        if(gifDecoder.getFrameCount() > 1){
                                                if(drawThread == null){
                                                        drawThread = new DrawThread();
                                                        drawThread.start();
                                                }
                                        }else{
                                                reDraw();
                                        }
                                }
                                break;
                        case SYNC_DECODER:
                                if(frameIndex == 1){
                                        currentImage = gifDecoder.getImage();
                                        reDraw();
                                }else if(frameIndex == -1){
                                        reDraw();
                                }else{
                                        if(drawThread == null){
                                                drawThread = new DrawThread();
                                                drawThread.start();
                                        }
                                }
                                break;
                        }
 
                }else{
                        Log.e("gif","parse error");
                }
                
        }
    }
    
    private void reDraw(){
        if(redrawHandler != null){
                        Message msg = redrawHandler.obtainMessage();
                        redrawHandler.sendMessage(msg);
        }
        
    }
    
    private void drawImage(){
        setImageBitmap(currentImage);
        invalidate();
    }
     
    private Handler redrawHandler = new Handler(){
        public void handleMessage(Message msg) {
            try{
                    if(backView != null){
                    backView.setBackgroundDrawable(new BitmapDrawable(currentImage));
                }else{
                    drawImage();
                }
            }catch(Exception ex){
                Log.e("GifView", ex.toString());
            }
        }
    };
    
    /**
     * 动画线程
     * @author liao
     *
     */
    private class DrawThread extends Thread{    
        public void run(){
                if(gifDecoder == null){
                        return;
                }
                while(isRun){
                   
                if (pause == false) {
                    GifFrame frame = gifDecoder.next();

                    if (frame == null) {
                        SystemClock.sleep(50);
                        continue;
                    }
                    if (frame.image != null)
                        currentImage = frame.image;
                    else if (frame.imageName != null) {
                        currentImage = BitmapFactory.decodeFile(frame.imageName);
                    }
                    long sp = frame.delay;
                    if (redrawHandler != null) {
                        reDraw();
                        SystemClock.sleep(sp);
                    } else {
                        break;
                    }
                } else {
                    SystemClock.sleep(50);
                        }
                }
        }
    }
    
}