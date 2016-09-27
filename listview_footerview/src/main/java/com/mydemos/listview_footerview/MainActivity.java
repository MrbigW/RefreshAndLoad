package com.mydemos.listview_footerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoadListView.ILoadListenner {
    private List<Genji> list;
    private LoadListView lv_main_list;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void getLoadData() {
        for (int i = 0; i < 3; i++) {
            Genji genji = new Genji();
            genji.setContent("这是特么更多加载的哟~~~");
            list.add(genji);
        }
    }

    public void showListView(List<Genji> list) {
        if (adapter == null) {
            lv_main_list = (LoadListView) findViewById(R.id.lv_main_list);
            lv_main_list.setInterface(this);
            adapter = new MyAdapter(this, list);
            lv_main_list.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }



    @Override
    public void onLoad() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //  获取更多数据
                getLoadData();
                //  更新listview显示
                showListView(list);
                lv_main_list.loadComplete();
            }
        }, 2000);
    }
}
