package com.example.searchview_library;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 笨货 on 2016/3/30.
 * 自定义的搜索View
 */
public class SearchView extends LinearLayout {

    private static final int HOT_ITEM = 1;
    private static final int HINT_ITEM = 2;
    private static final int HISTORY_ITEM = 3;
    private Context context;

    /**
     * 返回键
     */
    private ImageView iv_search_back;
    /**
     * 输入的文本框
     */
    private EditText et_search_content;
    /**
     * 清除搜索框内容
     */
    private ImageView iv_search_clear;
    /**
     * 搜索的按钮
     */
    private TextView tv_search_search;
    /**
     * 提示的列表
     */
    private ListView lv_search_hint;
    /**
     * 热门搜索
     */
    private NoScrollGridView gv_search_hot;
    /**
     * 搜索记录
     */
    private NoScrollGridView gv_search_history;

    private LinearLayout ll_item_delete;

    private LinearLayout ll_search_history;
    private LinearLayout ll_search_hot;
    private SharedPreferences sp;

    public SearchView(Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.search_view, this);
        initView();
    }


    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.search_view, this);
        initView();
    }

    private void initView() {
        iv_search_back = (ImageView) findViewById(R.id.iv_search_back);
        et_search_content = (EditText) findViewById(R.id.et_search_content);
        iv_search_clear = (ImageView) findViewById(R.id.iv_search_clear);
        tv_search_search = (TextView) findViewById(R.id.tv_search_search);
        lv_search_hint = (ListView) findViewById(R.id.lv_search_hint);
        gv_search_hot = (NoScrollGridView) findViewById(R.id.gv_search_hot);
        gv_search_history = (NoScrollGridView) findViewById(R.id.gv_search_history);
        ll_search_history = (LinearLayout) findViewById(R.id.ll_search_history);
        ll_search_hot = (LinearLayout) findViewById(R.id.ll_search_hot);
        ll_item_delete = (LinearLayout) findViewById(R.id.ll_item_delete);

        setListener();

        /**
         * 用SP存储保存搜索的历史纪录
         */
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }


    }

    /**
     * 设置监听
     */
    private void setListener() {
        iv_search_back.setOnClickListener(new SearchOnClickListener());
        iv_search_clear.setOnClickListener(new SearchOnClickListener());
        tv_search_search.setOnClickListener(new SearchOnClickListener());
        et_search_content.addTextChangedListener(new SearchTextChangedListener());
        /**
         * 热搜列表的Item点击监听
         */
        gv_search_hot.setOnItemClickListener(new listItemOnClickListener(HOT_ITEM));
        /**
         * 历史纪录的Item点击监听
         */
        gv_search_history.setOnItemClickListener(new listItemOnClickListener(HISTORY_ITEM));
        /**
         * 提示列表的Item点击监听
         */
        lv_search_hint.setOnItemClickListener(new listItemOnClickListener(HINT_ITEM));

        /**
         * 监听软键盘的搜索按钮
         */
        et_search_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startSearch(et_search_content.getText().toString());
                }
                return true;
            }
        });

        ll_item_delete.setOnClickListener(new SearchOnClickListener());
    }

    /**
     * 点击监听的回调
     */
    class SearchOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.iv_search_back) {
                ((Activity) context).finish();

            } else if (i == R.id.iv_search_clear) {
                et_search_content.setText(null);

            } else if (i == R.id.tv_search_search) {
                startSearch(et_search_content.getText().toString());

            } else if (i == R.id.ll_item_delete) {
                clearHistoryRecord();

            }
        }
    }

    /**
     * 历史纪录的Adapter
     */
    private CommonAdapter historyAdapter;

    private boolean isAutoKeep = false;//默认不保存

    private List<String> historySearchDatas;

    /**
     * 设置是否自动保存搜索记录
     *
     * @param isAutoKeep
     */
    public void keepSearchHistory(boolean isAutoKeep) {
        this.isAutoKeep = isAutoKeep;

        //如果没有设为自动保存，则返回
        if (!isAutoKeep) {
            return;
        }

        String history_search_datas = sp.getString("history_search_datas", "");
        if (!TextUtils.isEmpty(history_search_datas)) {//不为空
            historySearchDatas = new Gson().fromJson(history_search_datas, List.class);
        } else {//SP中没有数据，直接返回
            return;
        }

        if (historyAdapter == null && isAutoKeep) {
            ll_search_history.setVisibility(VISIBLE);
            historyAdapter = new CommonAdapter(context, historySearchDatas);
            gv_search_history.setAdapter(historyAdapter);
        }
    }

    private int maxHistoryRecordCount = 6;

    /**
     * 设置最大的历史纪录数，默认为6条
     *
     * @param maxHistoryRecordCount
     */
    public void setMaxHistoryRecordCount(int maxHistoryRecordCount) {
        this.maxHistoryRecordCount = maxHistoryRecordCount;
    }

    /**
     * 保存历史纪录
     *
     * @param text
     */
    private void keepSearchRecord(String text) {
        if (historySearchDatas == null) {
            historySearchDatas = new ArrayList<>(maxHistoryRecordCount);
            historySearchDatas.add(0, text);
        } else {
            for (int i = 0; i < historySearchDatas.size(); i++) {
                if ((historySearchDatas.get(i).equals(text))) {//判断数据是否已在历史记录中
                    //如果存在就移除
                    historySearchDatas.remove(i);
                }
            }

            //超过最大值
            if (historySearchDatas.size() >= maxHistoryRecordCount) {
                //移除最老的一条记录
                historySearchDatas.remove(maxHistoryRecordCount - 1);
            }
            //新添加的记录放在集合的首位
            historySearchDatas.add(0, text);

            //保存搜索的历史记录到sp中
            sp.edit().putString("history_search_datas", new Gson().toJson(historySearchDatas)).commit();

        }

        if (historyAdapter == null) {
            historyAdapter = new CommonAdapter(context, historySearchDatas);
            gv_search_history.setAdapter(historyAdapter);
        } else {
            historyAdapter.updateRecordList(historySearchDatas);
        }
    }

    class listItemOnClickListener implements AdapterView.OnItemClickListener {

        private int tag;
        private List<String> datas;

        public listItemOnClickListener(int tag) {
            this.tag = tag;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (tag == HOT_ITEM) {
                datas = hotSearchDatas;
            } else if (tag == HINT_ITEM) {
                datas = hintDatas;
            } else if (tag == HISTORY_ITEM) {
                datas = historySearchDatas;
            }

            String item = datas.get(position);
            et_search_content.setText(item);
            startSearch(item);
        }
    }

    /**
     * 刷新热搜和提示列表的状态
     */
    private void refreshListState() {
        et_search_content.setSelection(et_search_content.getText().length());
        lv_search_hint.setVisibility(GONE);
        ll_search_hot.setVisibility(GONE);
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 清空历史纪录
     */
    private void clearHistoryRecord() {
        if (historySearchDatas != null && historyAdapter != null) {
            historySearchDatas.clear();//清空
            historyAdapter.updateRecordList(historySearchDatas);
            ll_search_history.setVisibility(GONE);
            //删除SP存储的历史纪录
            sp.edit().putString("history_search_datas", "").commit();
        }
    }

    /**
     * 提示的listView的Adapter
     */
    private HintAdapter hintAdapter;
    /**
     * 热门搜索的Adapter
     */
    private CommonAdapter hotAdapter;

    private List<String> hotSearchDatas;

    /**
     * 设置热搜的数据集合
     *
     * @param hotSearchDatas
     */
    public void setHotSearchDatas(List<String> hotSearchDatas) {
        this.hotSearchDatas = hotSearchDatas;
        if (hotSearchDatas != null && hotSearchDatas.size() > 0) {
            ll_search_hot.setVisibility(VISIBLE);
            hotAdapter = new CommonAdapter(context, hotSearchDatas);
            gv_search_hot.setNumColumns(hotNumColumns);//设置列数
            gv_search_hot.setAdapter(hotAdapter);//设置热搜数据
        } else {
            ll_search_hot.setVisibility(GONE);
        }
    }

    private List<String> hintDatas;

    /**
     * 更新提示的数据集合
     *
     * @param hintDatas
     */
    public void updateHintList(List<String> hintDatas) {
        this.hintDatas = hintDatas;
        if (hintDatas != null && hintAdapter == null) {
            hintAdapter = new HintAdapter(context, hintDatas, maxHintLines);
            lv_search_hint.setAdapter(hintAdapter);
        } else {
            hintAdapter.notifyRefresh(hintDatas);
        }
    }

    private int maxHintLines = -1;

    /**
     * 设置提示的最大显示行数,默认显示所有
     *
     * @param maxHintLines
     */
    public void setMaxHintLines(int maxHintLines) {
        this.maxHintLines = maxHintLines;
    }

    private int hotNumColumns = 2;

    /**
     * 设置热搜数据的显示列数，默认为2列
     * 需要在setHotSearchDatas()前设置
     *
     * @param hotNumColumns
     */
    public void setHotNumColumns(int hotNumColumns) {
        this.hotNumColumns = hotNumColumns;
    }

    /**
     * 开始执行搜索方法
     *
     * @param text
     */
    private void startSearch(String text) {
        if (onSearchListener != null) {
            onSearchListener.onSearch(text);
            refreshListState();
            if (isAutoKeep) {
                //保存搜索记录
                keepSearchRecord(text);
            }
        }
    }

    /**
     * 监听EditText文本的改变
     */
    class SearchTextChangedListener implements TextWatcher {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        /**
         * 当文本改变时调用
         *
         * @param s
         * @param start
         * @param before
         * @param count
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s.toString().trim())) {//搜索框不为空
                ll_search_hot.setVisibility(GONE);
                ll_search_history.setVisibility(GONE);
                iv_search_clear.setVisibility(VISIBLE);//显示清除搜索框内容的按钮
                lv_search_hint.setVisibility(VISIBLE);
            } else {
                iv_search_clear.setVisibility(GONE);
                lv_search_hint.setVisibility(GONE);
                if (hotSearchDatas != null && hotSearchDatas.size() > 0) {
                    ll_search_hot.setVisibility(VISIBLE);
                }
                if (historyAdapter != null && historySearchDatas.size() > 0 && isAutoKeep) {
                    ll_search_history.setVisibility(VISIBLE);
                }
            }

            //刷新提示列表的数据
            if (onSearchListener != null) {
                onSearchListener.onRefreshHintList(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    /**
     * 自定义searchview的监听
     */
    public interface OnSearchListener {
        /**
         * 当进行搜索时回调此方法
         *
         * @param searchText 进行搜索的文本
         */
        void onSearch(String searchText);

        /**
         * 当输入框文本变化，需刷新提示的ListView时调用
         *
         * @param changedText 改变后的文本
         */
        void onRefreshHintList(String changedText);
    }

    private OnSearchListener onSearchListener;

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }
}
