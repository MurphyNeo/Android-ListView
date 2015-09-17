package com.murphy.adapter;

import java.util.List;
import com.murphy.R;
import com.murphy.activity.ListViewActivity;
import com.murphy.entity.NewsBean;
import com.murphy.listview.NewsListView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {

	private List<NewsBean> newsList;
	private LayoutInflater inflater;

	public NewsAdapter(Context context, List<NewsBean> newsList, NewsListView listView) {
		this.newsList = newsList;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return newsList.size();
	}

	@Override
	public Object getItem(int position) {
		return newsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NewsBean newsBean = newsList.get(position);
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.news_item, null);
			holder.image = (ImageView)convertView.findViewById(R.id.news_item_image);
			holder.title = (TextView)convertView.findViewById(R.id.news_item_title);
			holder.content = (TextView)convertView.findViewById(R.id.news_item_content);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		//holder.image.setImageResource(R.drawable.ic_launcher);
		String imageUrl = newsBean.getImage();
		//给图片设置一个tag
		holder.image.setTag(imageUrl);
		//先从缓存中加载图片,没有则设置默认图片
		ListViewActivity.imageLoader.showImageFromCache(holder.image, imageUrl);
		holder.title.setText(newsBean.getTitle());
		holder.content.setText(newsBean.getContent());
		return convertView;
	}

	/**
	 * 更新adapter中的数据，自动同步展示到ListView中
	 * @param list
	 */
	public void onDataChange(List<NewsBean> list){
		newsList = list;
		this.notifyDataSetChanged();
	}

	private class ViewHolder {
		ImageView image;
		TextView title;
		TextView content;
	}

}
