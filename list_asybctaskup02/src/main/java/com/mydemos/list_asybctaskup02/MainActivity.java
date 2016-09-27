package com.mydemos.list_asybctaskup02;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyListView.ILoadListenner {
    static List<NewsBean> list;
    private MyListView lv_main_news;
    private static final String URLPATH = "http://www.imooc.com/api/teacher?type=4&num=30";
    private NewsAdapter adapter;
    private ProgressBar progressbar;
    private TextView tv_main_show;
    //  每页最多显示的数目
    private final static int SHOW_ITEM_MAX = 8;

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                list = (List<NewsBean>) msg.obj;
                showListView(list);
                progressbar.setVisibility(View.GONE);
                tv_main_show.setVisibility(View.GONE);
                lv_main_news.setVisibility(View.VISIBLE);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        setContentView(R.layout.activity_main);
        lv_main_news = (MyListView) findViewById(R.id.lv_main_news);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        tv_main_show = (TextView) findViewById(R.id.tv_main_show);


        new Thread() {
            public void run() {
                Message msg = Message.obtain();
                msg.what = 0x123;
                msg.obj = getPortJsonData(URLPATH);
                handler.sendMessage(msg);
            }
        }.start();


    }

    public void showListView(List<NewsBean> list) {
        if (adapter == null) {
            lv_main_news.setInterface(this);
            adapter = new NewsAdapter(MainActivity.this, list);
            lv_main_news.setAdapter(adapter);
        } else {
            adapter.onDataChange(list);
        }
    }

    /**
     * 获取最大item数目的Json数据
     *
     * @param Url
     * @return
     */
    int tmp = 0;

    private List<NewsBean> getPortJsonData(String Url) {
        List<NewsBean> newsList = new ArrayList<>();
        String jsonString = null;
        try {
            jsonString = readStream(new URL(Url).openStream());
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if(SHOW_ITEM_MAX > jsonArray.length()) {
                for (int i = 0; i <jsonArray.length() ; i++) {
                    getNewsList(newsList,jsonArray,jsonObject,i);
                }
            }else {
                if(tmp + SHOW_ITEM_MAX >= jsonArray.length()) {
                    for (int i = 0; i <jsonArray.length() ; i++) {
                        getNewsList(newsList,jsonArray,jsonObject,i);
                    }
                }else {
                    for (int i = 0; i < tmp + SHOW_ITEM_MAX; i++) {
                        getNewsList(newsList,jsonArray,jsonObject,i);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tmp += SHOW_ITEM_MAX;
        return newsList;
    }


    /**
     * 为newsList赋值
     * @param newsList
     * @param jsonArray
     * @param jsonObject
     * @param count
     */
    private void getNewsList(List<NewsBean> newsList,JSONArray jsonArray,JSONObject jsonObject,int count){
        try {
            jsonObject = jsonArray.getJSONObject(count);
            NewsBean newsBean = new NewsBean();
            newsBean.setNews_IconUrl(jsonObject.getString("picSmall"));
            newsBean.setNews_Title(jsonObject.getString("name"));
            newsBean.setNews_Content(jsonObject.getString("description"));
            newsList.add(newsBean);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过Url将返回的数据解析成json格式数据，并转换为我们所封装的NewsBean
     *
     * @param Url
     * @return
     */
    private List<NewsBean> getJsonData(String Url) {
        List<NewsBean> newsList = new ArrayList<>();
        try {

            String jsonString = readStream(new URL(Url).openStream());


            //  Gson截下Json,需要变量名一一对应
//            Gson gson = new Gson();
//            gson.fromJson(jsonString,new TypeToken<NewsBean>(){}.getType());

//              原生API解析Json
            //  得到返回的JsonObj;
            JSONObject jsonObject = new JSONObject(jsonString);

            //  得到JsonObj对象中data的JsonArray
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            //  得到JsonArray中的数据并将其赋给NewsBean对象
            NewsBean newsBean;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                newsBean = new NewsBean();
                newsBean.setNews_IconUrl(jsonObject.getString("picSmall"));
                newsBean.setNews_Title(jsonObject.getString("name"));
                newsBean.setNews_Content(jsonObject.getString("description"));
                newsList.add(newsBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsList;
    }

    /**
     * 将从服务器得到的输入流传进来读取
     *
     * @param is
     * @return
     */
    private String readStream(InputStream is) {
        InputStreamReader isr = null;
        StringBuilder sb = new StringBuilder("");
        BufferedReader br = null;
        String line = "";
        try {
            isr = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }


    @Override
    public void onLoad() {
        new AsyncTask<String, Void, List<NewsBean>>() {
            @Override
            protected List<NewsBean> doInBackground(String... params) {
                SystemClock.sleep(2000);
                return getPortJsonData(params[0]);
            }

            @Override
            protected void onPostExecute(List<NewsBean> newsBeen) {
                super.onPostExecute(newsBeen);
                showListView(newsBeen);
                lv_main_news.loadComplete();
            }
        }.execute(URLPATH);
    }


    @Override
    public void onReflash() {

        new AsyncTask<String, Void, List<NewsBean>>() {


            @Override
            protected List<NewsBean> doInBackground(String... params) {
                tmp = 0;
                SystemClock.sleep(2000);
                return getPortJsonData(params[0]);
            }

            @Override
            protected void onPostExecute(List<NewsBean> newsBeen) {
                super.onPostExecute(newsBeen);
                showListView(newsBeen);
                lv_main_news.reflashComplete();
            }
        }.execute(URLPATH);
    }

}





