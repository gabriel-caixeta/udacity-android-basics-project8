package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {
    private static final String LOG_TAG = "MainActivity";
//    private static String GUARDIAN_URL = "https://content.guardianapis.com/search?show-fields=byline&&q=debates&api-key=test";
    private static String GUARDIAN_URL = "https://content.guardianapis.com/search";
    private static final int LOADER_ID = 0;

    ArticleAdapter articleAdapter;
    TextView stateTextView;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stateTextView = (TextView) findViewById(R.id.status_text);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ArrayList<Article> articles = new ArrayList<>();

        ListView articlesList = (ListView) findViewById(R.id.list);

        articleAdapter = new ArticleAdapter(this, articles);

        articlesList.setOnItemClickListener((parent, view, position, id) -> {
            Article article = articleAdapter.getItem(position);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(article.getUrl()));
            startActivity(i);
        });

        articlesList.setAdapter(articleAdapter);

        android.app.LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, this);

    }

    private void bindArticles(List<Article> articles) {
        articleAdapter.clear();
        articleAdapter.addAll(articles);
    }

    private void networkUnavailable() {
        Log.i(LOG_TAG, "No network available");
        progressBar.setVisibility(View.GONE);
        stateTextView.setText(R.string.no_internet);
    }

    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<List<Article>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader");
        startLoading();

        if (!hasNetworkConnection()) {
            networkUnavailable();
            return null;
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String category = sharedPref.getString(
                getString(R.string.settings_search_categories_key),
                getString(R.string.settings_search_categories_default));
        String orderBy = sharedPref.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri uriBase = Uri.parse(GUARDIAN_URL);
        Uri.Builder uriBuilder = uriBase.buildUpon();

//        https://content.guardianapis.com/search?show-fields=byline&order-by=newest&q=world&api-key=test
        uriBuilder.appendQueryParameter("show-fields", "byline");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("q", category);
        uriBuilder.appendQueryParameter("api-key", "test");

        // TODO build url
//        String url = GUARDIAN_URL;
        Log.i(LOG_TAG, uriBuilder.toString());
        return new ArticleLoader(MainActivity.this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> data) {
        Log.i(LOG_TAG, "onLoadFinished");
        progressBar.setVisibility(View.GONE);

        if (data != null && !data.isEmpty()) {
            bindArticles(data);
        } else {
            stateTextView.setText(R.string.no_results);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Article>> loader) {
        Log.i(LOG_TAG, "onLoaderReset");
        bindArticles(new ArrayList<>());
    }

    public Boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        Network currentNetwork = connectivityManager.getActiveNetwork();
        return currentNetwork != null;
    }
}