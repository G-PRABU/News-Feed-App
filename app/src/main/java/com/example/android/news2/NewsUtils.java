package com.example.android.news2;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NewsUtils {
    private static final String LOG_TAG = "NewsUtils";
    private static final String NEWS_JSON_RESPONSE = "response";
    private static final String NEWS_JSON_RESULTS = "results";
    private static final String NEWS_SECTION_NAME = "sectionName";
    private static final String NEWS_WEB_PUBLICATION_DATE = "webPublicationDate";
    private static final String NEWS_WEB_TITLE = "webTitle";
    private static final String NEWS_WEB_URL = "webUrl";
    private static final String NEWS_FIELDS = "fields";
    private static final String NEWS_THUMBNAIL = "thumbnail";
    private static final String NEWS_TAGS = "tags";
    private static final int SET_CONNECTION_TIME_OUT = 15000;
    private static final int SET_READ_TIME_OUT = 10000;
    private static final String SET_REQUEST_METHOD = "GET";
     private static List<News> extractFeatureFromJson(String newsJson){
        if(TextUtils.isEmpty(newsJson)){
            return null;
        }
        List<News> news = new ArrayList<>();
        try {
            JSONObject baseJsonObject = new JSONObject(newsJson);
            JSONObject baseJsonResponse = baseJsonObject.optJSONObject(NEWS_JSON_RESPONSE);
            JSONArray jsonResults = baseJsonResponse.optJSONArray(NEWS_JSON_RESULTS);
            for(int i = 0;i<jsonResults.length();i++){
                JSONObject currentNews = jsonResults.optJSONObject(i);
                String sectionName = currentNews.optString(NEWS_SECTION_NAME);
                String publishedOn = currentNews.optString(NEWS_WEB_PUBLICATION_DATE);
                String title = currentNews.optString(NEWS_WEB_TITLE);
                String url = currentNews.optString(NEWS_WEB_URL);
                JSONObject fields = currentNews.optJSONObject(NEWS_FIELDS);
                String imgUrl=null;
                if(fields!=null)
                    imgUrl = fields.optString(NEWS_THUMBNAIL);
                JSONArray tagsArray = currentNews.optJSONArray(NEWS_TAGS);
                JSONObject tagsObject = tagsArray.optJSONObject(0);
                String webTitle = null;
                if(tagsObject != null)
                    webTitle = tagsObject.optString(NEWS_WEB_TITLE);
                news.add(new News(sectionName,title,url,publishedOn,webTitle,imgUrl));
            }
        }catch(JSONException e){
            Log.e(LOG_TAG,"Error in extracting json : "+e);
        }
        return news;
    }

    public static List<News> fetchNews(String requestUrl){
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = httpRequest(url);
        } catch(IOException e){
            Log.e(LOG_TAG,"Error in making http request : "+e);
        }
        List<News> news  = extractFeatureFromJson(jsonResponse);
        return news;
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder outputString = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                outputString.append(line);
                line = reader.readLine();
            }
        }
        return outputString.toString();
    }

    private static String httpRequest(URL url) throws IOException {
        String jsonResponse = " ";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(SET_READ_TIME_OUT);
            urlConnection.setConnectTimeout(SET_CONNECTION_TIME_OUT);
            urlConnection.setRequestMethod(SET_REQUEST_METHOD);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Response Error  :" + urlConnection.getResponseCode());
            }
        } catch(IOException e) {
            Log.e(LOG_TAG,"Error in getting Json Response", e );
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static URL createUrl(String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch(MalformedURLException e){
            Log.e(LOG_TAG,"Error in creating url : "+e);
        }
        return url;
    }

}
