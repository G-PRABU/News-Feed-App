package com.example.android.news2;

public class News {

    private String mNewsUrl;
    private String mNewsSection;
    private String mNewsTitle;
    private String mNewsPublishedDate;
    private String mNewsWebTitle;
    private String mNewsImageUrl;

    public News(String section,String title,String url,String publishedDate,String webTitle,String imgUrl){
        mNewsSection = section;
        mNewsTitle = title;
        mNewsUrl = url;
        mNewsPublishedDate = publishedDate;
        mNewsWebTitle = webTitle;
        mNewsImageUrl = imgUrl;
    }

    public String getNewsWebTitle() {
        return mNewsWebTitle;
    }

    public String getNewsSection() {
        return mNewsSection;
    }

    public String getImageUrl() {
        return mNewsImageUrl;
    }

    public String getNewsTitle() {
        return mNewsTitle;
    }

    public String getNewsUrl() {
        return mNewsUrl;
    }

    public String getNewsPublishedDate() {
        return mNewsPublishedDate;
    }
}

