package com.example.newsapp;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {
    public ArticleAdapter(@NonNull Context context, @NonNull List<Article> articles) {
        super(context, 0, articles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View currentView, @NonNull ViewGroup parent) {
        if (currentView == null) {
            currentView = LayoutInflater.from(getContext()).inflate(R.layout.article_list_item, parent, false);
        }

        Article currentArticle = getItem(position);

        TextView titleTextView = (TextView) currentView.findViewById(R.id.article_title_text);
        titleTextView.setText(currentArticle.getTitle());

        TextView sectionTextView = (TextView) currentView.findViewById(R.id.article_section);
        String sectionText = ">" + currentArticle.getSection();
        sectionTextView.setText(sectionText);
        sectionTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        if (currentArticle.hasAuthor()) {
            TextView authorTextView = (TextView) currentView.findViewById(R.id.article_author_text);
            authorTextView.setText(currentArticle.getAuthor());
        }

        if (currentArticle.hasDate()) {
            String articleDateTime = currentArticle.getDate();

            TextView dateTextView = (TextView) currentView.findViewById(R.id.article_date_text);
            dateTextView.setText(getDate(articleDateTime));
        }

        return currentView;
    }

    private String getDate(String articleDateTime) {
        String date = "";
        if (articleDateTime.contains("T")) {
            date += articleDateTime.split("T")[0];

            date += ", " + articleDateTime.split("T")[1].split("Z")[0];
        } else {
            return articleDateTime;
        }
        if (date == "") {
            return articleDateTime;
        }
        return date;
    }
}
