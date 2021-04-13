package com.example.newsapp;

public class Article {
    private String url;
    private String title;
    private String date;
    private String author;
    private String section;
    private Boolean hasDate = false;
    private Boolean hasAuthor = false;

    public Article(String url, String title, String section, String author, String date) {
        this.url = url;
        this.title = title;
        this.section = section;

        if (author != null) {
            this.author = author;
            hasAuthor = true;
        }
        if (date != null) {
            this.date = date;
            hasDate = true;
        }
    }

    public Boolean hasAuthor() { return hasAuthor; }

    public Boolean hasDate() { return hasDate; }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getSection() { return section; }

    public String getAuthor() {
        if (hasAuthor) {
            return author;
        }
        return null;
    }

    public String getDate() {
        if (hasDate) {
            return date;
        }
        return null;
    }
}