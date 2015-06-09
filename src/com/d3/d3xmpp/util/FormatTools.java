package com.d3.d3xmpp.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class FormatTools {
	private static FormatTools tools = new FormatTools();

	public static FormatTools getInstance() {
		if (tools == null) {
			tools = new FormatTools();
			return tools;
		}
		return tools;
	}

	public Bitmap byteToBitmap(String imageBase64) {
		byte[] base64Bytes = Base64.decode(imageBase64.getBytes(), Base64.DEFAULT);
		Bitmap b = BitmapFactory.decodeByteArray(base64Bytes, 0, base64Bytes.length);
		return b;
	}

	// 将InputStream转换成Bitmap
	public Bitmap InputStream2Bitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

	/** 登录头像转成字符串 */
	public String bitmaptoString(Bitmap bitmap, int bitmapQuality) {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, bitmapQuality, bStream);
		String string = Base64.encodeToString(bStream.toByteArray(), Base64.DEFAULT);
		return string;
	}

	// public Bitmap getSmallBitmap(String filePath,int with) {
	// Bitmap bm = null;
	// try {
	// final BitmapFactory.Options options = new BitmapFactory.Options();
	// options.inJustDecodeBounds = true;
	// BitmapFactory.decodeFile(filePath, options);
	//
	// // Calculate inSampleSize
	// options.inSampleSize = calculateInSampleSize(options, with, with);
	//
	// // Decode bitmap with inSampleSize set
	// options.inJustDecodeBounds = false;
	//
	// bm = BitmapFactory.decodeFile(filePath, options);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return bm;
	//
	// }

	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
		}

		return inSampleSize;
	}

	/**
	 * 　　* 将base64转换成bitmap图片
	 * 
	 * 　　*
	 * 
	 * 　　* @param string base64字符串
	 * 
	 * 　　* @return bitmap
	 * 
	 * 　　
	 */

	public Bitmap stringtoBitmap(String string) {
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray = Base64.decode(string, Base64.DEFAULT);

			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

}
