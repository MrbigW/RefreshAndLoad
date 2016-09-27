package com.mydemos.swiperefresh;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipe_refresh;
    private List<Genji> list;
    private ListView lv_main_list;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  初始化SwipeRefreshLayout
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        //  为SwipeRefreshLayout设置监听事件
        swipe_refresh.setOnRefreshListener(this);

        //  可以为SwipeRefreshLayout设置刷新时的颜色变化，最多可设置4种
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            swipe_refresh.setColorSchemeResources(android.R.color.holo_blue_bright,android.R.color.holo_green_light,android.R.color.holo_orange_light,android.R.color.holo_red_light);
        }

        list = new ArrayList<>();
        getData();
        showListView(list);
    }

    public void getData() {
        for (int i = 0; i < 13; i++) {
            Genji genji = new Genji();
            genji.setContent("有机哟哟拉我酷烈~~");
            list.add(genji);
        }
    }
    public void getNewData() {
        for (int i = 0; i < 2; i++) {
            Genji genji = new Genji();
            genji.setContent("这是新刷新的数据哟~~");
            list.add(0,genji);
        }
    }

    public void showListView(List<Genji> list) {
        if (adapter == null) {
            lv_main_list = (ListView) findViewById(R.id.lv_main_list);
//            lv_main_list.setInterface(this);
            adapter = new MyAdapter(this, list);
            lv_main_list.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    //  实现onRefreshListener的onRefresh()方法
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //  获取数据
                getNewData();
                //  更新数据
                showListView(list);
                //  结束刷新
                swipe_refresh.setRefreshing(false);
            }
        },2000);
    }
}
