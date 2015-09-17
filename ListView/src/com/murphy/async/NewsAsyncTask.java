package com.murphy.async;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.murphy.entity.NewsBean;
import com.murphy.util.JsonUtil;

import android.os.AsyncTask;
import android.util.Log;

public abstract class NewsAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {

	/**
	 * ����һ���߳�ȥִ��������ʲ���,һ���ʱ���Ĳ���д�������������
	 */
	@Override
	protected List<NewsBean> doInBackground(String... params) {
		Log.d("tangb", params[0]);
		JSONObject obj = JsonUtil.getJson(params[0]);
		List<NewsBean> list = new ArrayList<NewsBean>();
		try {
			JSONArray arrays = obj.getJSONArray("data");
			for(int i = 0; i < arrays.length(); i++){
				JSONObject data = arrays.getJSONObject(i);
				NewsBean newsBean = new NewsBean();
				newsBean.setImage(data.getString("picSmall"));
				newsBean.setTitle(data.getString("name"));
				newsBean.setContent(data.getString("description"));
				list.add(newsBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * ��doInBackground��������ø÷�������������ݵ��÷���
	 * һ���ڸ÷����ж�UI���в�������������ص�UI��
	 */
	@Override
	protected void onPostExecute(List<NewsBean> result) {
		super.onPostExecute(result);
		//��������ʵ�ֵ�callBack��������UI
		callBack(result);
	}

	public abstract void callBack(List<NewsBean> result);

}
