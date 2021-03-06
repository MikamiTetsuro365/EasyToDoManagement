package com.example.TodoManagement.app.task;


import ch.qos.logback.classic.pattern.DateConverter;
import com.example.TodoManagement.entity.Task;
import com.example.TodoManagement.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.view.RedirectView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.lang.Long.max;

@Controller
@RequestMapping("/index")
public class TaskController {

    //ビジネスロジックにかいたサービスを使う
    private final TaskService taskService;
    @Autowired
    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    //タスクの並び
    private int order;

    //現在時刻から１分後が期日のデータを差し込む
    @GetMapping("/afteroneminute")
    public String aftrtone(TaskForm taskForm, Model model){

        Task task = new Task();
        //taskIdは自動で割り振られるのでスルー
        //UserIdは今は1で固定
        task.setUserId(1);
        Random random = new Random();
        task.setTypeId(random.nextInt(3));
        //ランダムな文字列の生成
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString().substring(0, 5);
        task.setTitle(str);
        task.setDetail(str);
        //時間(ミリビョウは切り捨て)
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.plusMinutes(1);
        task.setDeadline(localDateTime);
        System.out.println("aaa:" + localDateTime);
        //適当なの登録
        taskService.insert(task);
        return "redirect:/index";
    }

    //アクセス時はここ
    @GetMapping
    public String main(TaskForm taskForm, Model model){
        //順番更新
        taskForm.setIsNewTask(true);
        taskForm.setIsReInput(true);

        List<Task> list = new ArrayList<>();
        if(order == 0){
            model.addAttribute("complete", "登録順で並んでいます");
            list = taskService.findAll();
        }else if(order == 1){
            model.addAttribute("complete", "優先度順で並んでいます");
            list = taskService.findPriority();
        }else{
            model.addAttribute("complete", "期限順で並んでいます");
            list = taskService.findDeadline();
        }

        for(int i = 0; i < list.size(); i++){
            list.get(i).setRemainTime(remainTime(list.get(i).getDeadline()));
        }

        //フォームの内容は白紙に戻さずに現状維持
        model.addAttribute("taskForm", taskForm);
        model.addAttribute("list", list);
        //期限切れタスク
        List<Task> overdueList = taskService.ovrtdueTaskFindAll();
        model.addAttribute("overdueList", overdueList);
        if(overdueList.isEmpty()){
            model.addAttribute("overdue","期限切れタスクはありません");
        }else{
            model.addAttribute("overdue","期限切れタスクがあります");
        }
        model.addAttribute("title","タスク一覧");
        System.out.println("aaa");
        return "task/index";
    }

    //Ajaxでの更新処理
    @GetMapping("/ajax")
    public String ajax(TaskForm taskForm, Model model){
        //順番更新
        taskForm.setIsNewTask(true);
        taskForm.setIsReInput(true);

        List<Task> list = new ArrayList<>();
        if(order == 0){
            model.addAttribute("complete", "登録順で並んでいます");
            list = taskService.findAll();
        }else if(order == 1){
            model.addAttribute("complete", "優先度順で並んでいます");
            list = taskService.findPriority();
        }else{
            model.addAttribute("complete", "期限順で並んでいます");
            list = taskService.findDeadline();
        }

        for(int i = 0; i < list.size(); i++){
            list.get(i).setRemainTime(remainTime(list.get(i).getDeadline()));
        }

        //フォームの内容は白紙に戻さずに現状維持
        model.addAttribute("taskForm", taskForm);
        model.addAttribute("list", list);
        //期限切れタスク
        List<Task> overdueList = taskService.ovrtdueTaskFindAll();
        model.addAttribute("overdueList", overdueList);
        if(overdueList.isEmpty()){
            model.addAttribute("overdue","期限切れタスクはありません");
        }else{
            model.addAttribute("overdue","期限切れタスクがあります");
        }
        model.addAttribute("title","タスク一覧");
        System.out.println("bbb");
        return "block/tasklist::tasklist";
    }

    //期限切れのタスクの更新
    @GetMapping("/overdue{overdueId}")
    public String overdueUpdate(TaskForm taskForm, @PathVariable int overdueId, Model model){
        Optional<Task> otask = taskService.getOverdueTask(overdueId);
        Optional<TaskForm> oTaskForm = otask.map(t->outputTaskForm(t));
        if(oTaskForm.isPresent()){
            taskForm = oTaskForm.get();
        }

        model.addAttribute("taskForm", taskForm);
        //表示
        List<Task> list = taskService.findAll();
        for(int i = 0; i < list.size(); i++){
            list.get(i).setRemainTime(remainTime(list.get(i).getDeadline()));
        }
        model.addAttribute("list", list);
        //期限切れタスク
        List<Task> overdueList = taskService.ovrtdueTaskFindAll();
        model.addAttribute("overdueList", overdueList);
        if(overdueList.isEmpty()){
            model.addAttribute("overdue","期限切れタスクはありません");
        }else{
            model.addAttribute("overdue","期限切れタスクがあります");
        }
        //何順？
        if(order == 0){
            model.addAttribute("complete", "登録順で並んでいます");
        }else if(order == 1){
            model.addAttribute("complete", "優先度順で並んでいます");
        }else{
            model.addAttribute("complete", "期限順で並んでいます");
        }
        model.addAttribute("title","タスク一覧");
        //紐付け用
        model.addAttribute("taskId",overdueId);
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
        //System.out.println(taskForm.getDeadline());

        //表示
        List<Task> list = taskService.findAll();
        for(int i = 0; i < list.size(); i++){
            list.get(i).setRemainTime(remainTime(list.get(i).getDeadline()));
        }
        model.addAttribute("list", list);
        //期限切れタスク
        List<Task> overdueList = taskService.ovrtdueTaskFindAll();
        model.addAttribute("overdueList", overdueList);
        if(overdueList.isEmpty()){
            model.addAttribute("overdue","期限切れタスクはありません");
        }else{
            model.addAttribute("overdue","期限切れタスクがあります");
        }
        //何順？
        if(order == 0){
            model.addAttribute("complete", "登録順で並んでいます");
        }else if(order == 1){
            model.addAttribute("complete", "優先度順で並んでいます");
        }else{
            model.addAttribute("complete", "期限順で並んでいます");
        }
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
            taskForm.setIsReInput(false);
            //フォームの内容は白紙に戻さずに現状維持
            model.addAttribute("taskForm", taskForm);
            //表示
            List<Task> list = taskService.findAll();
            for(int i = 0; i < list.size(); i++){
                list.get(i).setRemainTime(remainTime(list.get(i).getDeadline()));
            }
            model.addAttribute("list", list);
            //期限切れタスク
            List<Task> overdueList = taskService.ovrtdueTaskFindAll();
            model.addAttribute("overdueList", overdueList);
            if(overdueList.isEmpty()){
                model.addAttribute("overdue","期限切れタスクはありません");
            }else{
                model.addAttribute("overdue","期限切れタスクがあります");
            }
            model.addAttribute("title","タスク一覧");

            return "task/index";
        }
    }

    //登録順
    @GetMapping("/register")
    public String registerOrder(TaskForm taskForm, Model model){
        //順番更新
        order = 0;
        taskForm.setIsNewTask(true);
        taskForm.setIsReInput(true);
        //フォームの内容は白紙に戻さずに現状維持
        List<Task> list = taskService.findAll();
        for(int i = 0; i < list.size(); i++){
            list.get(i).setRemainTime(remainTime(list.get(i).getDeadline()));
        }
        model.addAttribute("taskForm", taskForm);
        model.addAttribute("complete", "登録順で並んでいます");
        model.addAttribute("list", list);
        //期限切れタスク
        List<Task> overdueList = taskService.ovrtdueTaskFindAll();
        model.addAttribute("overdueList", overdueList);
        if(overdueList.isEmpty()){
            model.addAttribute("overdue","期限切れタスクはありません");
        }else{
            model.addAttribute("overdue","期限切れタスクがあります");
        }
        model.addAttribute("title","タスク一覧");
        return "task/index";
    }

    //優先度順
    @GetMapping("/priority")
    public String priorityOrder(TaskForm taskForm, Model model){
        //順番更新
        order = 1;
        taskForm.setIsNewTask(true);
        taskForm.setIsReInput(true);
        //フォームの内容は白紙に戻さずに現状維持
        List<Task> list = taskService.findPriority();
        for(int i = 0; i < list.size(); i++){
            list.get(i).setRemainTime(remainTime(list.get(i).getDeadline()));
        }
        model.addAttribute("taskForm", taskForm);
        model.addAttribute("complete", "優先度順で並んでいます");
        model.addAttribute("list", list);
        //期限切れタスク
        List<Task> overdueList = taskService.ovrtdueTaskFindAll();
        model.addAttribute("overdueList", overdueList);
        if(overdueList.isEmpty()){
            model.addAttribute("overdue","期限切れタスクはありません");
        }else{
            model.addAttribute("overdue","期限切れタスクがあります");
        }
        model.addAttribute("title","タスク一覧");
        return "task/index";
    }

    @GetMapping("/deadline")
    public String deadlineOrder(TaskForm taskForm, Model model){
        //期限
        order = 2;
        taskForm.setIsNewTask(true);
        taskForm.setIsReInput(true);
        //フォームの内容は白紙に戻さずに現状維持
        List<Task> list = taskService.findDeadline();
        for(int i = 0; i < list.size(); i++){
            list.get(i).setRemainTime(remainTime(list.get(i).getDeadline()));
        }
        model.addAttribute("taskForm", taskForm);
        model.addAttribute("complete", "期限順で並んでいます");
        model.addAttribute("list", list);
        //期限切れタスク
        List<Task> overdueList = taskService.ovrtdueTaskFindAll();
        model.addAttribute("overdueList", overdueList);
        if(overdueList.isEmpty()){
            model.addAttribute("overdue","期限切れタスクはありません");
        }else{
            model.addAttribute("overdue","期限切れタスクがあります");
        }
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
            //System.out.println("test2: taskID:"+taskId);
            taskService.update(task);
            //一度だけメッセージを残す
            redirectAttributes.addFlashAttribute("complete", "タスク変更したぞ");
            //リダイレクト
            //@GetMapping("/{id}")に戻る
            return "redirect:/index";
        }else{
            //失敗したら画面にとどまる
            taskForm.setIsReInput(false);
            model.addAttribute("taskForm", taskForm);
            //明示的にtaskIdは引き渡しておく
            model.addAttribute("taskId", taskId);
            //期限切れタスク
            List<Task> overdueList = taskService.ovrtdueTaskFindAll();
            model.addAttribute("overdueList", overdueList);
            if(overdueList.isEmpty()){
                model.addAttribute("overdue","期限切れタスクはありません");
            }else{
                model.addAttribute("overdue","期限切れタスクがあります");
            }
            model.addAttribute("title","タスク一覧");
            return "task/index";
        }
    }



    //期限前タスクの削除
    @PostMapping("/delete")
    public String delete(@RequestParam("taskId") int id, Model model){
        //タスク削除
        taskService.deleteById(id);
        //削除に失敗したら例外画面がでる
        return "redirect:/index";
    }

    //期限切れタスクの削除
    @PostMapping("/overduedelete")
    public String overdueDelete(@RequestParam("taskId") int id, Model model){
        //タスク削除
        taskService.overdueDeleteById(id);;
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
        taskForm.setDeadline(task.getDeadline());
        taskForm.getDetail();
        //新規登録画面に遷移はしないので
        taskForm.setIsNewTask(false);
        return taskForm;
    }

    private String remainTime(LocalDateTime deadline){
        String time;
        LocalDateTime now = LocalDateTime.now();

        long ms = ChronoUnit.MILLIS.between(now, deadline);

        if(ms >= 0){
            long d = 24 * 60 * 60 * 1000;
            long day  = (long) Math.floor(ms / d);
            long h = 60 * 60 * 1000;
            long hour = (long) Math.floor(ms / h);
            long hour1 = hour % 24;
            long m = 60 * 1000;
            long minute = (long) Math.floor((ms - hour * h) / m);
            long sec = (long) Math.floor((ms - (hour * h) - (minute * m)) / 1000);

            time = day + "日" + hour1 + "時間" + minute + "分" + sec + "秒";
        }else{
            time = 0 + "日" + 0 + "時間" + 0 + "分" + 0 + "秒";
        }
        System.out.println(time);

        return time;
    }
}
//    var overdue = new Date([[${t.deadline}]]);
//        var ms  = overdue.getTime() - now.getTime();
//        if(ms >= 0){
//        var d = 24 * 60 * 60 * 1000;
//        var day  = Math.floor(ms / d);
//        var h = 60 * 60 * 1000;
//        var hour = Math.floor((ms - day * d ) / h)
//        hour = hour % 24;
//        var m = 60 * 1000;
//        var minute = Math.floor((ms - day * d - hour * h) / m);
//        var sec = Math.floor((ms - (day * d) - (hour * h) - (minute * m)) / 1000);
//        document.getElementById("[[${t.id}]]").textContent = day + "日" + hour + "時間" + minute + "分" + sec + "秒";
//        }else{
//        document.getElementById("[[${t.id}]]").textContent = "期限を過ぎています";
//        }                            var overdue = new Date([[${t.deadline}]]);
