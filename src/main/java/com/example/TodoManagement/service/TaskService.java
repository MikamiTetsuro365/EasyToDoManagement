package com.example.TodoManagement.service;

import com.example.TodoManagement.entity.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<Task> findAll();
    List<Task> findPriority();
    List<Task> findDeadline();
    Optional<Task> getTask(int id);
    void insert(Task task);
    void update(Task task);
    void deleteById(int id);

    //期限切れのタスクに対して
    List<Task> overdueTaskFind(LocalDateTime localDateTime);
    List<Task> ovrtdueTaskFindAll();
    Optional<Task> getOverdueTask(int id);
    void overdueDeleteById(int id);
    void overdueTaskInsert(Task task);


}
