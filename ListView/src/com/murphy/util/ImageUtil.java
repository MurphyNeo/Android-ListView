package com.murphy.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtil {

	/**
	 * 从网络中获取图片
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmapFromUrl(String url){
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)u.openConnection();
			is = new BufferedInputStream(conn.getInputStream());
			bitmap = BitmapFactory.decodeStream(is);
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}
}
