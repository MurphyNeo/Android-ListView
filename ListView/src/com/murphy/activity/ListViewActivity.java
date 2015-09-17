package com.murphy.activity;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import com.murphy.R;
import com.murphy.adapter.NewsAdapter;
import com.murphy.async.NewsAsyncTask;
import com.murphy.entity.NewsBean;
import com.murphy.listview.NewsListView;
import com.murphy.listview.NewsListView.NewsListViewListener;
import com.murphy.util.ImageLoader;

public class ListViewActivity extends Activity implements NewsListViewListener {

	/*listview显示的数据集*/
	public static List<NewsBean> newsList = new ArrayList<NewsBean>();
    private NewsListView newsListView;
    private NewsAdapter adapter;
    /*图片加载类*/
    public static ImageLoader imageLoader;
    private static final String URL = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        newsListView = (NewsListView)findViewById(R.id.news_listview);
        //初始化图片加载(因为用了缓存,不能每次都去创建)
        imageLoader = new ImageLoader(newsListView);
        loadData(URL);
    }

    /**
     * 创建一个线程,获取URL返回的数据
     * @param url
     */
    private void loadData(String url){
		/*
		 * 实现callBack方法，将结果展示到UI上
		 */
        new NewsAsyncTask() {
            @Override
            public void callBack(List<NewsBean> result) {
                showData(result);
            }
        }.execute(url);
    }

    /**
     * 将结果展示到UI上
     * @param list
     */
    private void showData(List<NewsBean> list){
        newsList.addAll(list);
        if(adapter == null){
            //指定接口调用的子类，可以理解成IOC(在基类中调用子类的实现方法)
            newsListView.setInterface(this);
            adapter = new NewsAdapter(ListViewActivity.this, newsList, newsListView);
            //为ListView赋予Adapter
            newsListView.setAdapter(adapter);


        }else{
            //更新adapter中的数据，该数据会自动同步更新到ListView中显示
            adapter.onDataChange(newsList);
        }
        newsListView.loadSuccess();
    }

    /**
     * 在onScrollStateChanged空闲的时候执行该方法
     * 通过listview所处位置判断是刷新数据或者加载更多数据
     * 或者只是需要加载图片信息
     */
    @Override
    public void onLoad(int dataStatus, int start, int end) {
        String url = "";
        switch (dataStatus) {
            case NewsListView.DATA_UPDATE:
                newsList.clear();
                loadData(URL);
                break;
            case NewsListView.DATA_INSERT:
                url = "http://www.imooc.com/api/teacher?type=4&num=10";
                loadData(url);
                break;
            case NewsListView.PICTURE_LOAD:
                newsListView.loadSuccess();
                break;
            default:
                break;
        }
    }

}
