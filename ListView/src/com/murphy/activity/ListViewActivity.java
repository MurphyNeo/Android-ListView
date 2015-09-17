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

	/*listview��ʾ�����ݼ�*/
	public static List<NewsBean> newsList = new ArrayList<NewsBean>();
    private NewsListView newsListView;
    private NewsAdapter adapter;
    /*ͼƬ������*/
    public static ImageLoader imageLoader;
    private static final String URL = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        newsListView = (NewsListView)findViewById(R.id.news_listview);
        //��ʼ��ͼƬ����(��Ϊ���˻���,����ÿ�ζ�ȥ����)
        imageLoader = new ImageLoader(newsListView);
        loadData(URL);
    }

    /**
     * ����һ���߳�,��ȡURL���ص�����
     * @param url
     */
    private void loadData(String url){
		/*
		 * ʵ��callBack�����������չʾ��UI��
		 */
        new NewsAsyncTask() {
            @Override
            public void callBack(List<NewsBean> result) {
                showData(result);
            }
        }.execute(url);
    }

    /**
     * �����չʾ��UI��
     * @param list
     */
    private void showData(List<NewsBean> list){
        newsList.addAll(list);
        if(adapter == null){
            //ָ���ӿڵ��õ����࣬��������IOC(�ڻ����е��������ʵ�ַ���)
            newsListView.setInterface(this);
            adapter = new NewsAdapter(ListViewActivity.this, newsList, newsListView);
            //ΪListView����Adapter
            newsListView.setAdapter(adapter);


        }else{
            //����adapter�е����ݣ������ݻ��Զ�ͬ�����µ�ListView����ʾ
            adapter.onDataChange(newsList);
        }
        newsListView.loadSuccess();
    }

    /**
     * ��onScrollStateChanged���е�ʱ��ִ�и÷���
     * ͨ��listview����λ���ж���ˢ�����ݻ��߼��ظ�������
     * ����ֻ����Ҫ����ͼƬ��Ϣ
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
