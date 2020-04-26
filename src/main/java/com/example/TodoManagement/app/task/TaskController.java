package com.example.TodoManagement.app.task;


import com.example.TodoManagement.entity.Task;
import com.example.TodoManagement.service.TaskService;
import com.example.TodoManagement.service.TaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/index")
public class TaskController {

    //ビジネスロジックにかいたサービスを使う
    private final TaskService taskService;
    @Autowired
    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping
    public String showTask(TaskForm taskForm, Model model){

        //タスクの新規登録->新規登録画面を表示
        //System.out.println(taskForm.getOrder()); -> 初期値は0(登録順)
        taskForm.setIsNewTask(true);
        System.out.println(taskForm.getOrder());

        List<Task> list = taskService.findAll();

//        System.out.println("確認");
//        for(Task t: list){
//            System.out.println(t.getId());
//        }

        //タスクの一覧表示
        //持ってきた内容を表示
        model.addAttribute("list", list);
        model.addAttribute("complete", "登録順で並んでいます");
        model.addAttribute("title","タスク一覧");

        return "task/index";
    }

    //編集対象のデータを１件だけ取得してフォームに出力
    @GetMapping("/{id}")
    public String updateShow(TaskForm taskForm, @PathVariable int id, Model model){
        //PathVariableで/{id}のidを受け取る
        //idを元にタスクを受け取る
        Optional<Task> otask = taskService.getTask(id);
        //Optionalから出力のためにtaskFormに出力する
        //Optionalをtaskに otask -> task型のtを出して入れ直し
        Optional<TaskForm> oTaskForm = otask.map(t->outputTaskForm(t));

        if(oTaskForm.isPresent()){
            //NuLL出なければ, optinalから出す
            taskForm = oTaskForm.get();
        }

        //出力
        model.addAttribute("taskForm", taskForm);
        //表示
        List<Task> list = taskService.findAll();
        model.addAttribute("list", list);
        model.addAttribute("title","タスク一覧");
        //紐付け用
        model.addAttribute("taskId",id);
        return "task/index";
    }

    //追加
    @PostMapping("/insert")
    public String insertTask(@Validated @ModelAttribute TaskForm taskForm, BindingResult result, Model model){
        //POSTされたTaskFormの中身を取り出す
        //ModelAttribute->taskFormにPOSTされた内容が入る
        Task task = importTask(taskForm, 0);

        if(result.hasErrors() == false){
            //エラーがなければタスク登録をする
            taskService.insert(task);
            //System.out.println("ンホォ");
            //リダイレクト
            //二重クリック厳禁
            //特にredirectした後に情報を飛ばさないのでRedirectAttributesはいらない
            return "redirect:/index";
        }else{
            //もう一度新規登録ページへ
            taskForm.setIsNewTask(true);
            //フォームの内容は白紙に戻さずに現状維持
            model.addAttribute("taskForm", taskForm);
            //表示
            List<Task> list = taskService.findAll();
            model.addAttribute("list", list);
            model.addAttribute("title","タスク一覧");

            return "task/index";
        }
    }

    @GetMapping("/priority")
    public String registerOrder(TaskForm taskForm, Model model){
        taskForm.setIsNewTask(true);
        //フォームの内容は白紙に戻さずに現状維持
        List<Task> list = taskService.findPriority();
        model.addAttribute("taskForm", taskForm);
        model.addAttribute("complete", "優先度順で並んでいます");
        model.addAttribute("list", list);
        model.addAttribute("title","タスク一覧");
        return "task/index";
    }

    @GetMapping("/deadline")
    public String deadlineOrder(TaskForm taskForm, Model model){
        taskForm.setIsNewTask(true);
        //フォームの内容は白紙に戻さずに現状維持
        List<Task> list = taskService.findDeadline();
        model.addAttribute("taskForm", taskForm);
        model.addAttribute("complete", "期限順で並んでいます");
        model.addAttribute("list", list);
        model.addAttribute("title","タスク一覧");
        return "task/index";
    }

    //更新
    @PostMapping("/update")
    public String updateTask(@Validated @ModelAttribute TaskForm taskForm, BindingResult result, @RequestParam("taskId") int taskId,
                             Model model , RedirectAttributes redirectAttributes){
        //@RequestParmでpostされた結果を受け取る
        Task task = importTask(taskForm, taskId);
        System.out.println("test1: taskID:"+taskId);

        if(result.hasErrors() == false){
            //更新
            System.out.println("test2: taskID:"+taskId);
            taskService.update(task);
            //一度だけメッセージを残す
            redirectAttributes.addFlashAttribute("complete", "タスク変更したぞ");
            //リダイレクト
            //@GetMapping("/{id}")に戻る
            return "redirect:/index";
        }else{
            //失敗したら画面にとどまる
            model.addAttribute("taskForm", taskForm);
            model.addAttribute("title","タスク一覧");
            return "task/index";
        }

    }

    @PostMapping("/delete")
    public String delete(@RequestParam("taskId") int id, Model model){
        //タスク削除
        taskService.deleteById(id);
        //削除に失敗したら例外画面がでる
        return "redirect:/index";
    }


    private Task importTask(TaskForm taskForm, int taskId){
        Task task = new Task();
        //登録ならAutoIncrimentで自動的にIDが割り振られるのでtaskIdは特に設定しない
        if(taskId != 0){
            task.setId(taskId);
        }
        //ユーザーはそのうち作る
        task.setUserId(1);
        task.setTypeId(taskForm.getTypeId());
        task.setTitle(taskForm.getTitle());
        task.setDetail(taskForm.getDetail());
        task.setDeadline(taskForm.getDeadline());

        return task;
    }

    private TaskForm outputTaskForm(Task task){
        TaskForm taskForm = new TaskForm();
        taskForm.setTypeId(task.getTypeId());
        taskForm.setTitle(task.getTitle());
        taskForm.setDetail(task.getDetail());
        taskForm.getDetail();
        //新規登録画面に遷移はしないので
        taskForm.setIsNewTask(false);
        return taskForm;
    }


}
