package com.alamin.ecommerce.common;

import com.alamin.ecommerce.user.User;

public class Page {

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

    public Page(String pageTitle, String pageDescription, String pageKeywords, String pageImage, String url, String pageAuthor, String type, User user, int totalPage, int currentPage, int size, int o) {
        this.pageTitle = pageTitle;
        this.pageDescription = pageDescription;
        this.pageKeywords = pageKeywords;
        this.pageImage = pageImage;
        this.url = url;
        this.pageAuthor = pageAuthor;
        this.type = type;
        this.user = user;
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        this.size = size;
        this.o = o;
    }

    public Page() {
    }


    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageDescription() {
        return pageDescription;
    }

    public void setPageDescription(String pageDescription) {
        this.pageDescription = pageDescription;
    }

    public String getPageKeywords() {
        return pageKeywords;
    }

    public void setPageKeywords(String pageKeywords) {
        this.pageKeywords = pageKeywords;
    }

    public String getPageImage() {
        return pageImage;
    }

    public void setPageImage(String pageImage) {
        this.pageImage = pageImage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPageAuthor() {
        return pageAuthor;
    }

    public void setPageAuthor(String pageAuthor) {
        this.pageAuthor = pageAuthor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
