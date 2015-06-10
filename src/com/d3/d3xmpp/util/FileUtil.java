package com.d3.d3xmpp.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.d3.d3xmpp.constant.MyApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;

@SuppressLint("NewApi")
public class FileUtil {
	public final static int IMG = 0;
	public final static int SOUND = 1;
	public final static int APK = 2;
	public final static int PPT = 3;
	public final static int XLS = 4;
	public final static int DOC = 5;
	public final static int PDF = 6;
	public final static int CHM = 7;
	public final static int TXT = 8;
	public final static int MOVIE = 9;
	
	public static boolean changeFile(String oldFilePath,String newFilePath){
		return saveFileByBase64(getBase64StringFromFile(oldFilePath), newFilePath);
	}
	
	public static String getBase64StringFromFile(String imageFile)
	{
		// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try
		{
			in = new FileInputStream(imageFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		// 对字节数组Base64编码
		return Base64.encodeToString(data, Base64.DEFAULT);// 返回Base64编码过的字节数组字符
	}
	
	/**
	 * @param fileString base64
	 * @param filePath  保存路径,包括名字
	 * @return
	 */
	public static boolean saveFileByBase64(String fileString, String filePath) {
		// 对字节数组字符串进行Base64解码并生成图燿
		if (fileString == null) // 图像数据为空
			return false;
		byte[] data = Base64.decode(fileString, Base64.DEFAULT);
        saveFileByBytes(data, filePath);
//        MyApplication.getInstance().sendBroadcast(
//        		new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+filePath)));
        return true;
    }
	
//	/**
//	 * @param bitmap bitmap
//	 * @param filePath  保存路径,包括名字
//	 * @return
//	 */
//	public static boolean saveFileByBitmap(Bitmap bitmap, String filePath) {
//		// 对字节数组字符串进行Base64解码并生成图燿
//		if (bitmap == null) // 图像数据为空
//			return false;
//		byte[] data = ImageUtil.Bitmap2Bytes(bitmap);
//        saveFileByBytes(data, filePath);
//        return true;
//    }
//	
//	
	/**
	 * @param fileString bytes[]
	 * @param filePath  保存路径,包括名字
	 * @return
	 */
	public static boolean saveFileByBytes(byte[] data,String filePath) {
		// 对字节数组字符串进行Base64解码并生成图燿
        BufferedOutputStream stream = null;
        File file = null;
        try {
        	 file = new File(filePath);
             if (!file.exists()) {
 				File file2 = new File(filePath.substring(0, filePath.lastIndexOf("/")+1));
 				file2.mkdirs();
 			}
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return true;
    }
	
	/**
	 * imputStream 返回out
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static boolean saveFileByInputStream(InputStream inputStream,
			String filePath) {
		File file = null;
		file = new File(filePath);
		if (!file.exists()) {
			File file2 = new File(filePath.substring(0,
					filePath.lastIndexOf("/") + 1));
			file2.mkdirs();
		}
		FileOutputStream outputStream = null;
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		
		try {
			bis = new BufferedInputStream(inputStream);
			outputStream = new FileOutputStream(file);
			bos = new BufferedOutputStream(outputStream);
			int byte_ =0 ;
			while ((byte_ = bis.read()) != -1)
				bos.write(byte_);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally{
			try {
				bos.flush();
				bos.close();
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	
	/**
	 * 根据url得到名称
	 * @param url
	 * @return
	 */
	public static String getFileName(String url) {
		String fileName = "";
		if (url != null) {
			fileName = url.substring(url.lastIndexOf("/") + 1);
		}
		return fileName;
	}
	
	public static boolean renameFile(String path,String oldName,String newName){
		try {
			File file = null;
			file = new File(path + "/" + oldName);
			if (file.exists()) {
				file.renameTo(new File(path + "/" + newName));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 辅助方法，删除文件
	 * @param path
	 * @param context
	 * @param imageName
	 */
	private void delFile(String path, Context context,
			String imageName) {
		File file = null;
		String real_path = "";
		try {
			if (Util.getInstance().hasSDCard()) {
				real_path = (path != null && path.startsWith("/") ? path : "/"
						+ path);
			} else {
				real_path = Util.getInstance().getPackagePath(context)
						+ (path != null && path.startsWith("/") ? path : "/"
								+ path);
			}
			file = new File(real_path, imageName);
			if (file != null)
				file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//递归删除文件夹及文件
	 public static void RecursionDeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                RecursionDeleteFile(f);
            }
            file.delete();
        }
	    }
	
	/**
	 * 根据路径和名称都可以
	 * @param filePath
	 * @return
	 */
	public static int getType(String filePath){  
		if (filePath == null) {
			return -1;
		}
		String end ;
		if(filePath.contains("/")){
			File file = new File(filePath);
			if (!file.exists())
				return -1;
			/* 取得扩展名 */
				end = file
					.getName()
					.substring(file.getName().lastIndexOf(".") + 1,
							file.getName().length()).toLowerCase();
		}else{
			end = filePath.substring(filePath.lastIndexOf(".") + 1,
					filePath.length()).toLowerCase();;
		}
		
		end = end.trim().toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")
				|| end.equals("amr")) {
			return SOUND;
		} else if (end.equals("3gp") || end.equals("mp4")) {
			return MOVIE;
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			return IMG;
		} else if (end.equals("apk")) {
			return APK;
		} else if (end.equals("ppt")) {
			return PPT;
		} else if (end.equals("xls")) {
			return XLS;
		} else if (end.equals("doc")) {
			return DOC;
		} else if (end.equals("pdf")) {
			return PDF;
		} else if (end.equals("chm")) {
			return CHM;
		} else if (end.equals("txt")) {
			return TXT;
		} else {
			return -1;
		}
	} 
	
	
	
	public static Intent openFile(String filePath) {
		if (filePath == null) {
			return null;
		}
		File file = new File(filePath);
		if (!file.exists())
			return null;
		/* 取得扩展名 */
		String end = file
				.getName()
				.substring(file.getName().lastIndexOf(".") + 1,
						file.getName().length()).toLowerCase();
		end = end.trim().toLowerCase();
//		System.out.println(end);
		/* 依扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")
				|| end.equals("amr")) {
			return getAudioFileIntent(filePath);
		} else if (end.equals("3gp") || end.equals("mp4")) {
			return getAudioFileIntent(filePath);
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			return getImageFileIntent(filePath);
		} else if (end.equals("apk")) {
			return getApkFileIntent(filePath);
		} else if (end.equals("ppt")) {
			return getPptFileIntent(filePath);
		} else if (end.equals("xls")) {
			return getExcelFileIntent(filePath);
		} else if (end.equals("doc")) {
			return getWordFileIntent(filePath);
		} else if (end.equals("pdf")) {
			return getPdfFileIntent(filePath);
		} else if (end.equals("chm")) {
			return getChmFileIntent(filePath);
		} else if (end.equals("txt")) {
			return getTextFileIntent(filePath, false);
		} else {
			return getAllIntent(filePath);
		}
	}

	// Android获取一个用于打开APK文件的intent
	public static Intent getAllIntent(String param) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "*/*");
		return intent;
	}

	// Android获取一个用于打开APK文件的intent
	public static Intent getApkFileIntent(String param) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		return intent;
	}

	// Android获取一个用于打开VIDEO文件的intent
	public static Intent getVideoFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	// Android获取一个用于打开AUDIO文件的intent
	public static Intent getAudioFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	// Android获取一个用于打开Html文件的intent
	public static Intent getHtmlFileIntent(String param) {

		Uri uri = Uri.parse(param).buildUpon()
				.encodedAuthority("com.android.htmlfileprovider")
				.scheme("content").encodedPath(param).build();
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	// Android获取一个用于打开图片文件的intent
	public static Intent getImageFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	// Android获取一个用于打开PPT文件的intent
	public static Intent getPptFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	// Android获取一个用于打开Excel文件的intent
	public static Intent getExcelFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	// Android获取一个用于打开Word文件的intent
	public static Intent getWordFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	// Android获取一个用于打开CHM文件的intent
	public static Intent getChmFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	// Android获取一个用于打开文本文件的intent
	public static Intent getTextFileIntent(String param, boolean paramBoolean) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (paramBoolean) {
			Uri uri1 = Uri.parse(param);
			intent.setDataAndType(uri1, "text/plain");
		} else {
			Uri uri2 = Uri.fromFile(new File(param));
			intent.setDataAndType(uri2, "text/plain");
		}
		return intent;
	}

	// Android获取一个用于打开PDF文件的intent
	public static Intent getPdfFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}
	
}
