package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;

    @SerializedName("pageNumber")
    private int number;

    @SerializedName("pageSize")
    private int size;

    private boolean last;

    public PageResponse() {
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
