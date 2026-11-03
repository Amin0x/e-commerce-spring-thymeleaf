package com.alamin.ecommerce.frontend.dto;

import java.util.List;

/**
 * Thin mirror of a Spring {@code Page} JSON payload as returned by the backend
 * admin list endpoints ({@code content}, {@code page}, {@code size},
 * {@code totalElements}, {@code totalPages}).
 *
 * @param <T> the element type
 */
public class PageDto<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PageDto() {
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
