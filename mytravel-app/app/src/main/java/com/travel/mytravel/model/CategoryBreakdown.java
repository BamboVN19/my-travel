package com.travel.mytravel.model;

public class CategoryBreakdown {
    private String category;
    private double total;
    private double percentage;

    public CategoryBreakdown() {
    }

    public CategoryBreakdown(String category, double total, double percentage) {
        this.category = category;
        this.total = total;
        this.percentage = percentage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
