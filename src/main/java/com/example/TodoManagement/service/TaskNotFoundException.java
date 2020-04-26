package com.example.TodoManagement.service;

public class TaskNotFoundException extends RuntimeException {

    private static  final long serialVersionUID = 1L;
    //継承してるのでメッセージをオーバーライド
    //ビジネスロジック場でThrow
    public TaskNotFoundException(String message){
        super(message);
    }
}
