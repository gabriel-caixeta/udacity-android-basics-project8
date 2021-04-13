package com.example.newsapp;


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

/**
 * Helper methods related to requesting and receiving article data from the Guardianf.
 */
public final class QueryUtils {
    private static String TAG = "QueryUtils";

     /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Article> fetchArticles(String urlString) {
        ArrayList<Article> articles;

        URL url = createUrl(urlString);

        String responseJson = null;
        try {
            responseJson = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Http error", e);
        }

        articles = parseJsonResponse(responseJson);
        return articles;
    }

    private static URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.i(TAG, "Error with creating URL", e);
            e.printStackTrace();
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = null;

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(15000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "HTTP status: " + urlConnection.getResponseCode());
                return jsonResponse;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<Article> parseJsonResponse(String jsonResponse) {
        // Create an empty ArrayList that we can start adding Articles to
        ArrayList<Article> articles = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject response =  root.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            for (int i=0; i < results.length(); i++) {
                JSONObject articleData = results.getJSONObject(i);
                String title = articleData.getString("webTitle");
                String url = articleData.getString("webUrl");
                String date = articleData.getString("webPublicationDate");
                String section = articleData.getString("sectionName");

                String author;
                try {
                    JSONObject fields = articleData.getJSONObject("fields");
                    author = fields.getString("byline");
                } catch (Exception e) {
                    author = null;
                }

                articles.add(new Article(url, title, section, author, date));
            }
            // build up a list of Article objects with the corresponding data.

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        }

        // Return the list of articles
        return articles;
    }

}