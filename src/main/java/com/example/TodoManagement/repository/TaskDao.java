package com.example.TodoManagement.repository;

import com.example.TodoManagement.entity.Task;
import java.util.List;
import java.util.Optional;

public interface TaskDao {
    List<Task> findAll();
    List<Task> findPriority();
    List<Task> findDeadline();
    Optional<Task> findById(int id);
    void insert(Task task);
    int update(Task task);
    int deleteById(int id);
}
