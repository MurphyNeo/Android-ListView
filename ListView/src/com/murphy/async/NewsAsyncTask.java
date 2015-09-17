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
	 * 启动一个线程去执行网络访问操作,一般耗时长的操作写在这个方法里面
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
	 * 在doInBackground结束后调用该方法，将结果传递到该方法
	 * 一般在该访问中对UI进行操作，将结果返回到UI上
	 */
	@Override
	protected void onPostExecute(List<NewsBean> result) {
		super.onPostExecute(result);
		//调用字类实现的callBack方法更新UI
		callBack(result);
	}

	public abstract void callBack(List<NewsBean> result);

}
