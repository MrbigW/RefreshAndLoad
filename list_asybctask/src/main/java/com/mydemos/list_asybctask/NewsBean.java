package com.mydemos.list_asybctask;

/**
 * Created by Mr.W on 2016/9/6.
 */
public class NewsBean {
    private String news_IconUrl;
    private String news_Title;
    private String news_Content;


    public NewsBean() {
    }

    public NewsBean(String news_IconUrl, String news_Title, String news_Content) {
        this.news_IconUrl = news_IconUrl;
        this.news_Title = news_Title;
        this.news_Content = news_Content;
    }

    public String getNews_Title() {
        return news_Title;
    }

    public void setNews_Title(String news_Title) {
        this.news_Title = news_Title;
    }

    public String getNews_IconUrl() {
        return news_IconUrl;
    }

    public void setNews_IconUrl(String news_IconUrl) {
        this.news_IconUrl = news_IconUrl;
    }

    public String getNews_Content() {
        return news_Content;
    }

    public void setNews_Content(String news_Content) {
        this.news_Content = news_Content;
    }

    @Override
    public String toString() {
        return "NewsBean{" +
                "news_IconUrl='" + news_IconUrl + '\'' +
                ", news_Title='" + news_Title + '\'' +
                ", news_Content='" + news_Content + '\'' +
                '}';
    }

}
