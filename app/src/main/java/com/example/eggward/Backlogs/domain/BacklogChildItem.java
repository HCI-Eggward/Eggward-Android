package com.example.eggward.Backlogs.domain;

public class BacklogChildItem {
    private String todoContent;
    private String dateContent;

    public BacklogChildItem() {

    }

    public BacklogChildItem(String content, String dateContent) {

        this.todoContent = content;
        this.dateContent = dateContent;
    }

    public String getTodoContent() {
        return todoContent;
    }

    public String getDateContent() { return dateContent; }

    public void setTodoContent(String todoContent) {
        this.todoContent = todoContent;
    }

    public void setDateContent(String dateContent) { this.dateContent = dateContent; }
}
