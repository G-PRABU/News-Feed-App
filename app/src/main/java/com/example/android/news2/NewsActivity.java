package com.example.android.news2;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>,SwipeRefreshLayout.OnRefreshListener{

    private static final String BASE_GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";
    private static final String QUERY_APY_KEY = "api-key";
    private static final String QUERY_PARAM = "q";
    private static final String QUERY_THUMBNAIL = "show-fields";
    private static final String QUERY_AUTHOR = "show-tags";
    private static final String API_KEY = BuildConfig.THE_GUARDIAN_API_KEY;
    private static final String THUMBNAIL_VALUE = "thumbnail";
    private static final String AUTHOR_VALUE = "contributor";
    public static final String QUERY_ORDER_BY = "order-by";
    private NewsAdapter newsAdapter;
    private static final int NEWS_LOADER_ID = 1;
    @BindView(R.id.news_lv)
    ListView newsListView;
    @BindView(R.id.news_empty_tv)
    TextView newsEmptyTextView;
    @BindView(R.id.news_loading_indicator)
    View newsLoadingIndicator;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));
        swipeLayout.setOnRefreshListener(this);
        newsAdapter = new NewsAdapter(this,new ArrayList<News>());
        newsListView.setAdapter(newsAdapter);
        newsListView.setEmptyView(newsEmptyTextView);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            newsLoadingIndicator.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
        } else {
            newsLoadingIndicator.setVisibility(View.INVISIBLE);
            newsEmptyTextView.setText(getString(R.string.no_internet_connection));
        }
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News clickedNews = newsAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(clickedNews.getNewsUrl()));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        newsAdapter.clear();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        } else {
            swipeLayout.setRefreshing(false);
            newsEmptyTextView.setText(getString(R.string.no_internet_connection));
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));
        String  searchText = sharedPreferences.getString(getString(R.string.settings_search_key), getString(R.string.settings_search_default));
        Uri.Builder builder = Uri.parse(BASE_GUARDIAN_REQUEST_URL).buildUpon();
        builder.appendQueryParameter(QUERY_PARAM,searchText)
                .appendQueryParameter(QUERY_AUTHOR, AUTHOR_VALUE)
                .appendQueryParameter(QUERY_THUMBNAIL, THUMBNAIL_VALUE)
                .appendQueryParameter(QUERY_ORDER_BY, orderBy)
                .appendQueryParameter(QUERY_APY_KEY, API_KEY);
        Log.v("News Activity :"," Url "+builder.toString());
        return new NewsLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        newsLoadingIndicator.setVisibility(View.INVISIBLE);
        swipeLayout.setRefreshing(false);
        newsEmptyTextView.setText(getString(R.string.no_data_fond));
        newsAdapter.clear();
        if(data!=null && !data.isEmpty()){
            newsAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        newsAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.main_settings) {
            startActivity(new Intent(this,SettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
