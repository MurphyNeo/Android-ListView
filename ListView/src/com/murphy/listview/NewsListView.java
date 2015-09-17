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
 * 1、在SCROLL_STATE_IDLE时加载数据，这样滑动的时候不会有卡的情况
 * 2、下拉分页的时候,应先加载数据后，再去加载图片
 * 3、通过onTouchEvent来判断是哪个方向的滑动,是否需要更新数据,或者只是需要加载图片(不需要加载数据)
 * @author Administrator
 *
 */
public class NewsListView extends ListView implements OnScrollListener {
	
	private View header;
	private int headerHeight;//header布局高度
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
	 * 加载listview上拉以及下拉需要展示的UI
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
	 * 通知父布局，占用的宽高
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
	 * 设置header的上边距
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
		//如果在空闲的时候才做处理
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
			//如果不是空闲状态,取消所有图片处理任务
			ListViewActivity.imageLoader.cancelAllTasks();
		}
	}

	private int start;
	private int end;
	private boolean firstLoad;//是否第一次加载

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		firstItem = firstVisibleItem;
		lastItem = firstVisibleItem + visibleItemCount;
		total = totalItemCount;
		start = firstVisibleItem == 0 ? 0 : firstVisibleItem - 1;
		int visible = (firstVisibleItem == 0 || lastItem == totalItemCount) ? visibleItemCount -1 : visibleItemCount;
		end = start + visible;//ListView可见的项
		Log.d("tangb","list列表项:"+ListViewActivity.newsList.size()+"可见项:" + visibleItemCount +"总共项："+totalItemCount);
		//第一次显示的时候调用
		if(firstLoad && visibleItemCount > 0){
			ListViewActivity.imageLoader.loadImages(start, end);
			firstLoad = false;
		}
	}

	private int startY;// 摁下时的Y值

	/*
	 * 数据更新
	 */
	public static final int DATA_UPDATE = 0;
	/*
	 * 加载更多数据
	 */
	public static final int DATA_INSERT = 1;
	/*
	 * 加载图片
	 */
	public static final int PICTURE_LOAD = 2;

	private int dataStatus;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startY = (int) ev.getY();//获取Y值
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
			//如果Y值大于初始Y值,则为下拉操作,同时在第一项时,重新加载数据
			topPadding(topPadding);
			dataStatus = DATA_UPDATE;
		}else if(space < 0 && lastItem == total){
			//footerView.setVisibility(View.VISIBLE);
			//如果Y值小于初始Y值,则为上拉操作,同时在最后一项时,加载新数据
			bottomPadding(-topPadding);
			dataStatus = DATA_INSERT;
		}else{
			//在数据中间滑动时,则只加载图片
			dataStatus = PICTURE_LOAD;
		}
	}
	
	private void bottomPadding(int padding) {
		footer.setPadding(footer.getPaddingLeft(), header.getPaddingTop(),
				header.getPaddingRight(), padding);
		footer.invalidate();
	}

	/**
	 * 上滑刷新处理成功后调用,设置加载状态,同时将footer设置为不可见
	 * 在数据处理完毕后才进行图片加载
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
