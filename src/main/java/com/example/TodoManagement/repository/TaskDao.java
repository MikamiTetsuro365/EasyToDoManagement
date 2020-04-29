package com.example.TodoManagement.repository;

import com.example.TodoManagement.entity.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskDao {
    //期限前タスクの検索
    List<Task> findAll();
    List<Task> findPriority();
    List<Task> findDeadline();
    Optional<Task> findById(int id);
    void insert(Task task);
    int update(Task task);
    int deleteById(int id);

    //期限切れタスクの検索
    List<Task> overdueTaskFind(LocalDateTime localDateTime);
    List<Task> overdueTaskFindAll();
    void overdueTaskInsert(Task task);
}
