package com.example.TodoManagement.app.schedulingtask;
import com.example.TodoManagement.entity.Task;
import com.example.TodoManagement.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SheduledTasks {

    private final TaskService taskService;

    @Autowired
    public SheduledTasks(TaskService taskService){
        this.taskService = taskService;
    }

    @Scheduled(fixedRate = 5000)
    public void checkDeadline(){
        //System.out.println("test");
        //期限切れのタスクを探す
        LocalDateTime localDateTime = LocalDateTime.now();
        List<Task> list = taskService.overdueTaskFind(localDateTime);
        //期限切れのタスクをタスク一覧から削除して期限切れタスク一覧へ写す
        for (Task task : list){
            taskService.deleteById(task.getId());
            taskService.overdueTaskInsert(task);
        }
        System.out.println(list.size());
    }

}
