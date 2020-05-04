package com.example.TodoManagement.entity;

import java.time.LocalDateTime;

public class Task {

    //データベース上
    private int id;
    private  int userId;
    private int typeId;
    private TaskType taskType;
    private String title;
    private String detail;
    private LocalDateTime deadline;
    //残り時間
    private String remainTime;

    //セッターゲッター

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setRemainTime(String remainTime) {
        this.remainTime = remainTime;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getTypeId() {
        return typeId;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public String getRemainTime() {
        return remainTime;
    }

}
