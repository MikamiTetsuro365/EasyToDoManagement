package com.example.TodoManagement.app.task;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

//入力されたり取得したデータの一時保存場所
public class TaskForm {

    //バリデーションはしっかりと
    //1桁で少数桁0
    @NotNull(message = "入力しろ")
    @Digits(integer = 1, fraction = 0)
    private int typeId;

    @NotNull(message = "タイトルを入力しろ")
    @Size(min = 1, max=30, message="30文字以内で入力しろ")
    private String title;

    @NotNull(message = "詳細を入力しろ")
    private String detail;

    @NotNull(message = "期限を入力しろ")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    //未来日付であるか検証する
    @Future(message = "期限が過去に設定されてるぞ")
    private LocalDateTime deadline;

    //タスクの検索か追加，更新の判断
    private boolean isNewTask;
    //タスクの再入力をさせる場合
    private boolean isReInput;
    //タスクの並び
    private int order;


    //コンストラクタ
    public TaskForm(){}

    public TaskForm(int typeId, String title, String detail, LocalDateTime deadline, boolean isNewTask, int order, boolean isReInput) {
        this.typeId = typeId;
        this.title = title;
        this.detail = detail;
        this.deadline = deadline;
        this.isNewTask = isNewTask;
        this.order = order;
        this.isReInput = isReInput;
    }

    //ゲッター
    public int getTypeId() {
        return typeId;
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

    public boolean getIsNewTask() {
        return isNewTask;
    }

    public int getOrder(){return order; };

    public boolean getIsReInput() {
        return isReInput;
    }

    //セッター
    public void setTypeId(int typeId) {
        this.typeId = typeId;
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

    public void setIsNewTask(boolean newTask) {
        isNewTask = newTask;
    }

    public void setOrder(int order){this.order = order; }

    public void setIsReInput(boolean reInput) {
        this.isReInput = reInput;
    }

}
