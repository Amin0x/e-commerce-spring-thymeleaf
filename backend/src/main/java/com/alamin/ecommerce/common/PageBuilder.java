package com.alamin.ecommerce.common;

import com.alamin.ecommerce.user.User;

public class PageBuilder {
    private String pageTitle;
    private String pageDescription;
    private String pageKeywords;
    private String pageImage;
    private String url;
    private String pageAuthor;
    private String type;
    private User user;
    private int totalPage;
    private int currentPage;
    private int size;
    private int o;

    public PageBuilder setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }

    public PageBuilder setPageDescription(String pageDescription) {
        this.pageDescription = pageDescription;
        return this;
    }

    public PageBuilder setPageKeywords(String pageKeywords) {
        this.pageKeywords = pageKeywords;
        return this;
    }

    public PageBuilder setPageImage(String pageImage) {
        this.pageImage = pageImage;
        return this;
    }

    public PageBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public PageBuilder setPageAuthor(String pageAuthor) {
        this.pageAuthor = pageAuthor;
        return this;
    }

    public PageBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public PageBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public PageBuilder setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public PageBuilder setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public PageBuilder setSize(int size) {
        this.size = size;
        return this;
    }

    public PageBuilder setO(int o) {
        this.o = o;
        return this;
    }

    public Page createPage() {
        return new Page(pageTitle, pageDescription, pageKeywords, pageImage, url, pageAuthor, type, user, totalPage, currentPage, size, o);
    }
}