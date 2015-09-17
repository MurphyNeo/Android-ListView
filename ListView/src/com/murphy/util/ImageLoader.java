package com.murphy.util;

import java.util.HashSet;
import java.util.Set;

import com.murphy.R;
import com.murphy.activity.ListViewActivity;
import com.murphy.adapter.NewsAdapter;
import com.murphy.listview.NewsListView;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

public class ImageLoader {

	private ImageView imageView;
	private String imageUrl;
	private LruCache<String, Bitmap> cache;
	private NewsListView listView;
	private Set<ImageAsyncTask> tasks;

	public ImageLoader(NewsListView listView) {
		this.listView = listView;
		tasks = new HashSet<ImageAsyncTask>();
		//获取最大内存
		int maxMemory = (int)Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 4;
		cache = new LruCache<String, Bitmap>(cacheSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				//在每次放入缓存时调用,统计大小
				return value.getByteCount();
			}
		};
	}

	/**
	 * 将图片添加到缓存
	 * @param key
	 * @param value
	 */
	public void addCache(String key, Bitmap value){
		if(getCache(key) == null){
			cache.put(key, value);
		}
	}

	/**
	 * 在缓存中获取图片
	 * @param key
	 * @return
	 */
	public Bitmap getCache(String key){
		return cache.get(key);
	}

	/**
	 * 先看缓存中是否有，没有就给默认图片
	 * @param imageView
	 * @param url
	 */
	public void showImageFromCache(ImageView imageView, String url){
		Bitmap bitmap = getCache(url);
		if(bitmap == null){
			imageView.setImageResource(R.drawable.ic_launcher);
		}else{
			imageView.setImageBitmap(bitmap);
		}
	}

	/**
	 * 用来加载从start到end的图片
	 * @param start
	 * @param end
	 */
	public void loadImages(int start, int end){
		for(int i = start; i < end; i++){
			String url = ListViewActivity.newsList.get(i).getImage();
			Bitmap bitmap = getCache(url);
			if(bitmap == null){
				ImageAsyncTask task = new ImageAsyncTask(url);
				task.execute(url);
				tasks.add(task);
			}else{
				ImageView imageView = (ImageView) listView.findViewWithTag(url);
				if(imageView != null){
					imageView.setImageBitmap(bitmap);
				}

			}
		}
	}

	/**
	 * 取消所有任务
	 */
	public void cancelAllTasks() {
		if(tasks != null){
			for(ImageAsyncTask task : tasks){
				task.cancel(false);
			}
		}
	}

	class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
		private String url;

		public ImageAsyncTask(String url){
			this.url = url;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			String url = params[0];
			Bitmap bitmap = ImageUtil.getBitmapFromUrl(url);
			if(bitmap != null){
				addCache(url, bitmap);
			}
			return bitmap;
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			ImageView imageView = (ImageView) listView.findViewWithTag(url);
			if(imageView != null && result != null){
				imageView.setImageBitmap(result);
			}
			tasks.remove(this);
		}
	}
}
