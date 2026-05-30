package com.hotely5d.controller;

import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice //异常通知=>出现异常会执行
public class SysExceptionHandler {
    @ExceptionHandler(value = Exception.class) //描述该方法作为通知代码处理方法,表标识异常类型
    @ResponseBody
    public Result exceptionHandler(Exception e){
        e.printStackTrace();
        return new Result(StatusCode.ERROR,false,"出现异常了:"+e.getMessage());
    }

}

