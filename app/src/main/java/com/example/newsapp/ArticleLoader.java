package com.example.newsapp;


import android.content.AsyncTaskLoader;
import android.content.Context;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {
    String url;

    public ArticleLoader(@NonNull Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public List<Article> loadInBackground() {
        if (url == null) {
            return null;
        }
        return QueryUtils.fetchArticles(url);
//        return new ArrayList<>();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
