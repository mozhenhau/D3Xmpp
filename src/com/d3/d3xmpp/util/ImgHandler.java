package com.d3.d3xmpp.util;

import com.d3.d3xmpp.constant.MyApplication;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImgHandler {

	// Ô²½ÇÍ¼Æ¬
	public static Bitmap ToCircularCorner(Bitmap bitmap, int i) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff336699;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = i;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	// Ô²½ÇÍ¼Æ¬
	public static Bitmap ToCircular(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getWidth(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff000000;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
		final RectF rectF = new RectF(rect);

		final float roundPx = (bitmap.getWidth()) / 10;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setColor(color);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//		paint.setStyle(Style.STROKE);
//		paint.setStrokeWidth(2);
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	// Ô²ÐÎÍ¼Æ¬
	public static Bitmap ToCircularBig(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getWidth(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff000000;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
		final RectF rectF = new RectF(rect);

		final float roundPx = (bitmap.getWidth())/ 2;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setColor(color);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//			paint.setStyle(Style.STROKE);
//			paint.setStrokeWidth(2);
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Drawable toCircularDrawable(Bitmap bitmap) {
		BitmapDrawable drawable = new BitmapDrawable(null,
				ToCircular(bitmap));
		return drawable;
	}
	
	/**
	 * Ô²½Ç
	 * @param context
	 * @param id
	 * @return
	 */
	public static Drawable ToCircular(int id) {
		BitmapDrawable drawable = new BitmapDrawable(null,
				ToCircular(drawableToBitmap(MyApplication.getInstance().getResources().getDrawable(
						id))));
		return drawable;
	}
	
	/**
	 * Ô²ÐÎ
	 * @param context
	 * @param id
	 * @return
	 */
	public static Drawable ToCircularBig(int id) {
		BitmapDrawable drawable = new BitmapDrawable(null,
				ToCircularBig(drawableToBitmap(MyApplication.getInstance().getResources().getDrawable(
						id))));
		return drawable;
	}
	

	// drawable-->bitmap
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	// ¼Óµ¹Ó°
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 0;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	public static Bitmap myImg(Bitmap bitmap, int i) {
		return ImgHandler.createReflectionImageWithOrigin(ImgHandler
				.ToCircularCorner(bitmap, i));
	}
}
