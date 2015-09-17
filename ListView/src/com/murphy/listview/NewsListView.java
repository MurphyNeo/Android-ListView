package com.murphy.listview;

import com.murphy.R;
import com.murphy.activity.ListViewActivity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 1����SCROLL_STATE_IDLEʱ�������ݣ�����������ʱ�򲻻��п������
 * 2��������ҳ��ʱ��,Ӧ�ȼ������ݺ���ȥ����ͼƬ
 * 3��ͨ��onTouchEvent���ж����ĸ�����Ļ���,�Ƿ���Ҫ��������,����ֻ����Ҫ����ͼƬ(����Ҫ��������)
 * @author Administrator
 *
 */
public class NewsListView extends ListView implements OnScrollListener {
	
	private View header;
	private int headerHeight;//header���ָ߶�
	private View footer;
	private View footerView;
	private int total;
	private int firstItem;
	private int lastItem;

	public static String[] URLS;

	public NewsListView(Context context) {
		super(context);
		initView(context);
	}

	public NewsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public NewsListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	/**
	 * ����listview�����Լ�������Ҫչʾ��UI
	 * @param context
	 */
	private void initView(Context context) {
		firstLoad = true;
		LayoutInflater inflater = LayoutInflater.from(context);
		header = inflater.inflate(R.layout.news_header, null);
		measureView(header);
		headerHeight = header.getMeasuredHeight();
		topPadding(-headerHeight);
		this.addHeaderView(header);
		footer = inflater.inflate(R.layout.news_footer, null);
		footerView = footer.findViewById(R.id.news_load_layout);
		//footerView.setVisibility(View.VISIBLE);
		this.addFooterView(footer);
		this.setOnScrollListener(this);
	}

	/**
	 * ֪ͨ�����֣�ռ�õĿ��
	 *
	 * @param view
	 */
	private void measureView(View view) {
		ViewGroup.LayoutParams p = view.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int height;
		int tempHeight = p.height;
		if (tempHeight > 0) {
			height = MeasureSpec.makeMeasureSpec(tempHeight,
					MeasureSpec.EXACTLY);
		} else {
			height = MeasureSpec.makeMeasureSpec(tempHeight,
					MeasureSpec.UNSPECIFIED);
		}
		view.measure(width, height);
	}

	/**
	 * ����header���ϱ߾�
	 *
	 * @param topPadding
	 */
	private void topPadding(int topPadding) {
		header.setPadding(header.getPaddingLeft(), topPadding,
				header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//����ڿ��е�ʱ���������
		if (scrollState == SCROLL_STATE_IDLE) {
			listener.onLoad(dataStatus, start, end);
			switch (dataStatus) {
				case DATA_UPDATE:
					Log.d("tangb", "DATA_UPDATE");
					topPadding(-headerHeight);
					break;
				case DATA_INSERT:
					Log.d("tangb", "DATA_INSERT");
					break;
				case PICTURE_LOAD:
					Log.d("tangb", "PICTURE_LOAD");
					break;
				default:
					break;
			}
		}else{
			//������ǿ���״̬,ȡ������ͼƬ��������
			ListViewActivity.imageLoader.cancelAllTasks();
		}
	}

	private int start;
	private int end;
	private boolean firstLoad;//�Ƿ��һ�μ���

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		firstItem = firstVisibleItem;
		lastItem = firstVisibleItem + visibleItemCount;
		total = totalItemCount;
		start = firstVisibleItem == 0 ? 0 : firstVisibleItem - 1;
		int visible = (firstVisibleItem == 0 || lastItem == totalItemCount) ? visibleItemCount -1 : visibleItemCount;
		end = start + visible;//ListView�ɼ�����
		Log.d("tangb","list�б���:"+ListViewActivity.newsList.size()+"�ɼ���:" + visibleItemCount +"�ܹ��"+totalItemCount);
		//��һ����ʾ��ʱ�����
		if(firstLoad && visibleItemCount > 0){
			ListViewActivity.imageLoader.loadImages(start, end);
			firstLoad = false;
		}
	}

	private int startY;// ����ʱ��Yֵ

	/*
	 * ���ݸ���
	 */
	public static final int DATA_UPDATE = 0;
	/*
	 * ���ظ�������
	 */
	public static final int DATA_INSERT = 1;
	/*
	 * ����ͼƬ
	 */
	public static final int PICTURE_LOAD = 2;

	private int dataStatus;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startY = (int) ev.getY();//��ȡYֵ
				break;
			case MotionEvent.ACTION_MOVE:
				onMove(ev);
				break;
			case MotionEvent.ACTION_UP:
				break;
			default:
				break;
		}
		return super.onTouchEvent(ev);
	}

	private void onMove(MotionEvent ev){
		int tempY = (int) ev.getY();
		int space = tempY - startY;
		int topPadding = space - headerHeight;
		if(space > 0 && firstItem ==0){
			//���Yֵ���ڳ�ʼYֵ,��Ϊ��������,ͬʱ�ڵ�һ��ʱ,���¼�������
			topPadding(topPadding);
			dataStatus = DATA_UPDATE;
		}else if(space < 0 && lastItem == total){
			//footerView.setVisibility(View.VISIBLE);
			//���YֵС�ڳ�ʼYֵ,��Ϊ��������,ͬʱ�����һ��ʱ,����������
			bottomPadding(-topPadding);
			dataStatus = DATA_INSERT;
		}else{
			//�������м们��ʱ,��ֻ����ͼƬ
			dataStatus = PICTURE_LOAD;
		}
	}
	
	private void bottomPadding(int padding) {
		footer.setPadding(footer.getPaddingLeft(), header.getPaddingTop(),
				header.getPaddingRight(), padding);
		footer.invalidate();
	}

	/**
	 * �ϻ�ˢ�´���ɹ������,���ü���״̬,ͬʱ��footer����Ϊ���ɼ�
	 * �����ݴ�����Ϻ�Ž���ͼƬ����
	 */
	public void loadSuccess(){
		footerView.setVisibility(View.GONE);
		ListViewActivity.imageLoader.loadImages(start, end);
	}

	private NewsListViewListener listener;

	public interface NewsListViewListener {
		public void onLoad(int dataStatus, int start, int end);
	}

	public void setInterface(NewsListViewListener listener){
		this.listener = listener;
	}

}
