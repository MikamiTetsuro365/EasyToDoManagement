package com.example.TodoManagement.config;

import com.example.TodoManagement.service.TaskNotFoundException;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;

import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;

//全てのコントローラに対する共通処理
@ControllerAdvice
public class WEBMvcControllerAdvice {

    //空文字をNullに変更
    @InitBinder
    public void initBinder(WebDataBinder dateBinder){
        dateBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

//    //例外処理
//    //特定の例外に対応した処理
//    @ExceptionHandler(EmptyResultDataAccessException.class)
//    public String handleException(EmptyResultDataAccessException e, Model model){
//        model.addAttribute("message", e);
//        return "error/CustamPage";
//    }
//
    //全てのコントローラ対象のエラー処理
    @ExceptionHandler(TaskNotFoundException.class)
    public String handleException(TaskNotFoundException e, Model model){
        model.addAttribute("message", e);
        return "error/errorPage";
    }


}
