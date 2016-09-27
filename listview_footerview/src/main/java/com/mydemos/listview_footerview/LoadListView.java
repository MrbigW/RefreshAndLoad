package com.mydemos.listview_footerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Mr.W on 2016/9/8.
 */
public class LoadListView extends ListView implements AbsListView.OnScrollListener {

    private View footer;

    int totalItemCount;     //  总数量;
    int lastVisibleItem;    //  最后一个可见的item

    boolean isLoading;  //正在加载

    private ILoadListenner iLoadListenner;

    public LoadListView(Context context) {
        super(context);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 添加底部加载提示到listview
     *
     * @param context
     */
    private void initView(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        footer = layoutInflater.inflate(R.layout.footer_layout, null);
        footer.findViewById(R.id.load_layout).setVisibility(GONE);
        this.addFooterView(footer);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (totalItemCount == lastVisibleItem && scrollState == SCROLL_STATE_IDLE) {
            if (!isLoading) {
                isLoading = true;
                footer.findViewById(R.id.load_layout).setVisibility(VISIBLE);
                //  加载更多
                iLoadListenner.onLoad();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    public void loadComplete(){
        isLoading = false;
        footer.findViewById(R.id.load_layout).setVisibility(GONE);
    }

    public void setInterface(ILoadListenner iLoadListenner){
        this.iLoadListenner = iLoadListenner;
    }

    //  加载更多数据的回调接口
    public interface ILoadListenner {
        public void onLoad();
    }

}
