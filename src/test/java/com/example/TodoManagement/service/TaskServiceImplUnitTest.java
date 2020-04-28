package com.example.TodoManagement.service;

import com.example.TodoManagement.repository.TaskDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.TodoManagement.entity.Task;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Mockitoを使う
//スタブクラスを差し込める
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskServiceImplの単体テストをします")
public class TaskServiceImplUnitTest {

    //スタブ
    @Mock
    private TaskDao dao;

    //テスト対象となるクラス
    @InjectMocks
    private TaskServiceImpl taskServiceImpl;

    //テストケースが存在しない時
    @Test
    @DisplayName("Taskの全件取得をして見て，0件の場合どうかテスト")
    void testFindAllEmpty(){
        //空（期待する結果）
        List<Task> list = new ArrayList();

        //モッククラスにセット
        //thenReturnに期待する返り値
        Mockito.when(dao.findAll()).thenReturn(list);
        //一旦実行
        List<Task> actualList = taskServiceImpl.findAll();

        //指定回数だけ実行してみる
        Mockito.verify(dao, Mockito.times(1)).findAll();

        //戻り値を検査してみる
        //何も戻ってきていなかったら成功
        assertEquals(0, actualList.size());
    }

    //テストケースが存在する時
    @Test
    @DisplayName("Taskの全件取得をして見て，5件あるかどうかテスト")
    void testFindAll(){
        //空（期待する結果）
        List<Task> list = Arrays.asList(
                new Task(),
                new Task()
        );

        //モッククラスにセット
        //thenReturnに期待する返り値
        Mockito.when(dao.findAll()).thenReturn(list);
        //一旦実行
        List<Task> actualList = taskServiceImpl.findAll();

        //指定回数だけ実行してみる
        Mockito.verify(dao, Mockito.times(1)).findAll();

        //戻り値を検査してみる
        //何も戻ってきていなかったら成功
        assertEquals(2, actualList.size());
    }

}
