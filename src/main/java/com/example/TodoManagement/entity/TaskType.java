package com.example.TodoManagement.entity;

public class TaskType {

    private int id;
    private String type;
    private String comment;

    public void setId(int id){
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public String getComment() {
        return comment;
    }

    public int getId(){
        return id;
    }



}
