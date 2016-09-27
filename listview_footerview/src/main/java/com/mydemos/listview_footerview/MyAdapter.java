package com.mydemos.listview_footerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.W on 2016/9/7.
 */
public class MyAdapter extends BaseAdapter {
    private Context context;
    List<Genji> list = new ArrayList<>();

    public MyAdapter(Context context, List<Genji> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_main, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.content = (TextView) convertView.findViewById(R.id.textView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        viewHolder.content.setText(list.get(position).getContent());

        return convertView;
    }

    class ViewHolder {
        private ImageView imageView;
        private TextView content;
    }
}
