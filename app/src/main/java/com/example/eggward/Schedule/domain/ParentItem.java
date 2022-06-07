package com.example.eggward.Schedule.domain;

import java.util.ArrayList;

public class ParentItem {
    private String categoryTitle;

    public ParentItem() {

    }

    public ParentItem(String category) {
        this.categoryTitle = category;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }
}
