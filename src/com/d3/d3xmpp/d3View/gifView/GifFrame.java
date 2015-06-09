package com.d3.d3xmpp.d3View.gifView;

import android.graphics.Bitmap;

public class GifFrame {
        /**
         * 构造函数
         * @param im 图片
         * @param del 延时
         */
        public GifFrame(Bitmap im, int del) {
                image = im;
                delay = del;
        }
        
        public GifFrame(String name,int del){
            imageName = name;
            delay = del;
        }
        
        /**图片*/
        public Bitmap image;
        /**延时*/
        public int delay;
        /**当图片存成文件时的文件名*/
        public String imageName = null;
        
        /**下一帧*/
        public GifFrame nextFrame = null;
}