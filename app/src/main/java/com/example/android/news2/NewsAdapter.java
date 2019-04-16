package com.example.android.news2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context activity, ArrayList<News> news){
        super(activity,0,news);
    }

    @BindView(R.id.section_tv) TextView sectionNameTextView;
    @BindView(R.id.title_tv) TextView titleTextView;
    @BindView(R.id.published_date_tv) TextView publishTextView;
    @BindView(R.id.web_title_tv) TextView webTitle;
    @BindView(R.id.news_iv) ImageView newsImageView;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newsListView = convertView;
        if(newsListView == null){
            newsListView = LayoutInflater.from(getContext()).inflate(R.layout.news_list,parent,false);
        }
        News currentNews = getItem(position);
        ButterKnife.bind(this,newsListView);
        sectionNameTextView.setText(currentNews.getNewsSection());
        titleTextView.setText(currentNews.getNewsTitle());
        publishTextView.setText(currentNews.getNewsPublishedDate().substring(0,10));
        if(currentNews.getNewsWebTitle() != null) {
            String author = getContext().getString(R.string.list_author) + " " + currentNews.getNewsWebTitle();
            webTitle.setText(author);
        }
        if(currentNews.getImageUrl()!=null)
            Picasso.with(getContext()).load(currentNews.getImageUrl()).into(newsImageView);
        return newsListView;
    }
}
