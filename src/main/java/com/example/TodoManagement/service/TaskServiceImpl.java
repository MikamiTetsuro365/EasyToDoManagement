package com.example.TodoManagement.service;

import com.example.TodoManagement.entity.Task;
import com.example.TodoManagement.repository.TaskDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    //インターフェスのTaskDaoを使ってTaskDaoImplの機能を呼び出して利用する
    private final TaskDao taskDao;
    @Autowired
    public TaskServiceImpl(TaskDao taskDao){
        this.taskDao = taskDao;
    }

    @Override
    //タスク全件取得
    public List<Task> findAll() {
        return taskDao.findAll();
    }

    @Override
    public List<Task> findPriority() {
        return taskDao.findPriority();
    }

    @Override
    public List<Task> findDeadline() {
        return taskDao.findDeadline();
    }

    @Override
    public List<Task> overdueTaskFind(LocalDateTime localDateTime) {
        return taskDao.overdueTaskFind(localDateTime);
    }

    @Override
    public List<Task> ovrtdueTaskFindAll() {
        return taskDao.overdueTaskFindAll();
    }

    @Override
    public void overdueTaskInsert(Task task) {
        taskDao.overdueTaskInsert(task);
    }

    @Override
    //タスク1件取得
    public Optional<Task> getTask(int id) {
        //タスクを取得してなければ例外飛ばす
        try{
            return taskDao.findById(id);
        }catch (EmptyResultDataAccessException e){
            throw new TaskNotFoundException("タスクがないにゃ");
        }
    }

    @Override
    public Optional<Task> getOverdueTask(int id) {
        //タスク取得してなければ例外を飛ばす
        try{
            return taskDao.overdueFindById(id);
        }catch (EmptyResultDataAccessException e){
            throw new TaskNotFoundException("タスクがないにゃ");
        }
    }

    @Override
    //タスク追加
    public void insert(Task task) {
        taskDao.insert(task);
    }

    @Override
    //タスク追加
    public void update(Task task) {
        //更新
        int num = taskDao.update(task);
        System.out.println("test3: taskID:"+task.getId());
        if(num == 0){
            System.out.println("test4: num:"+num);
            //更新されなかったら例外をthrow
            throw new TaskNotFoundException("タスクがないにゃ" + num);
        }
        System.out.println("test5: num:"+num);
    }

    @Override
    //タスク削除
    public void deleteById(int id) {
        //更新
        if(taskDao.deleteById(id) == 0){
            //更新されなかったら例外をthrow
            throw new TaskNotFoundException("タスクがないにゃ");
        }
    }

    //期限切れタスクの削除
    @Override
    public void overdueDeleteById(int id) {
        //更新
        if(taskDao.overdueDeleteById(id) == 0){
            //更新されなかったら例外をthrow
            throw new TaskNotFoundException("タスクがないにゃ");
        }
    }

}
