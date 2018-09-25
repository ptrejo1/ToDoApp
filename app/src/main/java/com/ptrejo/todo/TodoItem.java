package com.ptrejo.todo;

import java.util.Date;

public class TodoItem {

    private String name;
    private String date;
    private String description;
    private String addInfo;
    private long id;

    public TodoItem() {}

    public TodoItem(String name, String date, String description, String addInfo, long id) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.addInfo = addInfo;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
