package com.example.eggward.Schedule.domain;

public class ChildItem {
    private String todoContent;

    public ChildItem() {

    }

    public ChildItem(String content) {
        this.todoContent = content;
    }

    public String getTodoContent() {
        return todoContent;
    }

    public void setTodoContent(String todoContent) {
        this.todoContent = todoContent;
    }
}
