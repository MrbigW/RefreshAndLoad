package com.mydemos.listview_headerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ReFlashListView.IreflashListener {
    private List<Genji> list;
    private ReFlashListView lv_main_list;
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

    public void getReflshData() {
        for (int i = 0; i < 3; i++) {
            Genji genji = new Genji();
            genji.setContent("这是刷新出来的数据哟~~");
            list.add(0,genji);
        }
    }

    public void showListView(List<Genji> list) {
        if (adapter == null) {
            lv_main_list = (ReFlashListView) findViewById(R.id.lv_main_list);
            lv_main_list.setInterface(this);
            adapter = new MyAdapter(this, list);
            lv_main_list.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onReflash() {


        Handler handler =new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //  获取最新数据
                getReflshData();
                //  通知界面改变
                showListView(list);

                lv_main_list.reflashComplete();
            }
        },2000);

    }
}
























