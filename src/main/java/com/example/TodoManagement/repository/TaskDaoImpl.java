package com.example.TodoManagement.repository;

import com.example.TodoManagement.entity.Task;
import com.example.TodoManagement.entity.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TaskDaoImpl implements TaskDao{

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public TaskDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Task> findAll() {
        //task_typeの外部キーtask_typeで結合
        String s = "SELECT task.id, user_id, type_id, title, detail, deadline, " +
                "type, comment FROM task INNER JOIN task_type ON task.type_id = task_type.id";
        //クエリの結果取り出し用
        List<Map<String, Object>> rlist = jdbcTemplate.queryForList(s);
        return importTask(rlist);
    }

    @Override
    public List<Task> findPriority() {
        //task_typeの外部キーtask_typeで結合
        String s = "SELECT task.id, user_id, type_id, title, detail, deadline, " +
                "type, comment FROM task INNER JOIN task_type ON task.type_id = task_type.id ORDER BY type_id ASC, deadline ASC";
        //クエリの結果取り出し用
        List<Map<String, Object>> rlist = jdbcTemplate.queryForList(s);
        return importTask(rlist);
    }

    @Override
    public List<Task> findDeadline() {
        //task_typeの外部キーtask_typeで結合
        String s = "SELECT task.id, user_id, type_id, title, detail, deadline, " +
                "type, comment FROM task INNER JOIN task_type ON task.type_id = task_type.id ORDER BY deadline ASC";
        //クエリの結果取り出し用
        List<Map<String, Object>> rlist = jdbcTemplate.queryForList(s);
        return importTask(rlist);
    }

    @Override
    //タスクを1件だけ取得する
    public Optional<Task> findById(int id) {
        String s = "SELECT task.id, user_id, type_id, title, detail, deadline, " +
                "type, comment FROM task INNER JOIN task_type ON task.type_id = task_type.id WHERE task.id = ?";
        //
        Map<String, Object> r = jdbcTemplate.queryForMap(s, id);

        //ここむだ
        Task task = new Task();
        task.setId((int)r.get("id"));
        task.setUserId((int)r.get("user_id"));
        task.setTypeId((int)r.get("type_id"));
        task.setTitle((String)r.get("title"));
        task.setDetail((String)r.get("detail"));
        //クエリ結果はTimestamp型
        task.setDeadline(((Timestamp)r.get("deadline")).toLocalDateTime());

        TaskType taskType = new TaskType();
        taskType.setId((int)r.get("type_id"));
        taskType.setType((String)r.get("type"));
        taskType.setComment((String)r.get("comment"));
        //Task<-TaskType
        task.setTaskType(taskType);

        //Nullチェック
        Optional<Task> oTask = Optional.ofNullable(task);

        return oTask;
    }

    //期限ぎれタスクの検索
    @Override
    public List<Task> overdueTaskFind(LocalDateTime localDateTime) {
        String s = "SELECT task.id, user_id, type_id, title, detail, deadline, " +
                "type, comment FROM Task INNER JOIN task_type ON task.type_id = task_type.id WHERE deadline <= ?";
        //クエリの結果取り出し用
        //timestampに変換しないとSqlに埋め込めないので
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        List<Map<String, Object>> rlist = jdbcTemplate.queryForList(s, timestamp);
        return importTask(rlist);
    }

    //期限ぎれタスクを全て検索
    @Override
    public List<Task> overdueTaskFindAll() {
        String s = "SELECT overdueTask.id, user_id, type_id, title, detail, deadline, " +
                "type, comment FROM overdueTask INNER JOIN task_type ON overdueTask.type_id = task_type.id";
        //クエリの結果取り出し用
        List<Map<String, Object>> rlist = jdbcTemplate.queryForList(s);
        return importTask(rlist);
    }



    //期限切れタスクを期限切れタスク一覧に登録
    @Override
    public void overdueTaskInsert(Task task) {
        String s = "INSERT INTO overdueTask(user_id, type_id, title, detail, deadline) VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(s, task.getUserId(), task.getTypeId(), task.getTitle(), task.getDetail(), task.getDeadline());
    }


    @Override
    //タスクの追加
    public void insert(Task task) {
        //idはAutoIncriment
        String s = "INSERT INTO task(user_id, type_id, title, detail, deadline) VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(s, task.getUserId(), task.getTypeId(), task.getTitle(), task.getDetail(), task.getDeadline());
    }

    @Override
    //タスクの更新，更新されなければ0を返却
    public int update(Task task) {
        //String s = "UPDATE task SET type_id = ?, title = ?, detail = ?, deadline = ? WHERE id = ?";
        String s = "UPDATE task SET type_id = ?, title = ?, detail = ?, deadline = ? WHERE id = ?";
        return jdbcTemplate.update(s, task.getTypeId(), task.getTitle(), task.getDetail(), task.getDeadline(), task.getId());
    }

    @Override
    //タスクの削除
    public int deleteById(int id) {
        String s = "DELETE FROM task WHERE id = ?";
        return jdbcTemplate.update(s, id);
    }

    //検索->リスト追加
    private List<Task> importTask(List<Map<String, Object>> rlist){
        List<Task> list = new ArrayList<>();
        for(Map<String, Object> r : rlist){
            //ここむだ
            Task task = new Task();
            task.setId((int)r.get("id"));
            task.setUserId((int)r.get("user_id"));
            task.setTypeId((int)r.get("type_id"));
            task.setTitle((String)r.get("title"));
            task.setDetail((String)r.get("detail"));
            //クエリ結果はTimestamp型
            task.setDeadline(((Timestamp)r.get("deadline")).toLocalDateTime());

            TaskType taskType = new TaskType();
            taskType.setId((int)r.get("type_id"));
            taskType.setType((String)r.get("type"));
            taskType.setComment((String)r.get("comment"));
            //Task<-TaskType
            task.setTaskType(taskType);
            //追加
            list.add(task);
        }
        return list;
    }

}
