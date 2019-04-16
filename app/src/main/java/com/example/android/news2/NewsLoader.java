package com.example.android.news2;

import android.content.Context;
import android.content.AsyncTaskLoader;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String newsUrl;

    public NewsLoader(Context context, String url){
        super(context);
        newsUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if(newsUrl == null)
            return null;
        List<News> news = NewsUtils.fetchNews(newsUrl);
        return news;
    }
}

