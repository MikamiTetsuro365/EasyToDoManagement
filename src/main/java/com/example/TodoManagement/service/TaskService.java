package com.example.TodoManagement.service;

import com.example.TodoManagement.entity.Task;
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
}
