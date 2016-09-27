package com.mydemos.list_asybctask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mr.W on 2016/9/6.
 */
public class NewsAdapter extends BaseAdapter {
    private Context context;
    private List<NewsBean> mList;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;

    public NewsAdapter(Context context, List<NewsBean> data) {
        this.context = context;
        this.mList = data;
        mInflater = LayoutInflater.from(context);
        mImageLoader = new ImageLoader(context);
    }

    public void onDataChange(List<NewsBean> list){
        this.mList =list;
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_list_layout, null);

            viewHolder.iv_Icon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
            viewHolder.tv_Content = (TextView) convertView.findViewById(R.id.tv_item_content);
            viewHolder.tv_Title = (TextView) convertView.findViewById(R.id.tv_item_title);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String url = mList.get(position).getNews_IconUrl();
        viewHolder.iv_Icon.setImageResource(R.drawable.loading);
        viewHolder.iv_Icon.setTag(url);
//        new ImageLoader().showImageByThread(viewHolder.iv_Icon,mList.get(position).getNews_IconUrl());
        mImageLoader.showImageByAsyncTask(viewHolder.iv_Icon,mList.get(position).getNews_IconUrl());
        viewHolder.tv_Title.setText(mList.get(position).getNews_Title());
        viewHolder.tv_Content.setText(mList.get(position).getNews_Content());

        return convertView;
    }

    class ViewHolder {
        private TextView tv_Title, tv_Content;
        private ImageView iv_Icon;
    }
}


















