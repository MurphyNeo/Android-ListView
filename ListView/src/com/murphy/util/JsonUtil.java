package com.murphy.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONObject;

import android.util.Log;

public class JsonUtil {

	/**
	 * ��ȡ�����е�JSON���ݣ�����JSONObject����
	 * @param url
	 * @return
	 */
	public static JSONObject getJson(String url){
		JSONObject obj = null;
		try {
			String result = readStream(new URL(url).openStream());
			Log.d("JsonUtil", result);
			obj = new JSONObject(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	/**
	 * ��ȡ�����еķ��ؽ��
	 * @param is
	 * @return
	 */
	public static String readStream(InputStream is){
		InputStreamReader isr = null;
		BufferedReader br = null;
		String result = "";
		try {
			String line = "";
			isr = new InputStreamReader(is,"utf-8");
			br = new BufferedReader(isr);
			while((line = br.readLine()) != null){
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null){
					br.close();
				}
				if(isr != null){
					isr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
}
