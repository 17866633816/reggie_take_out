package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
public class GloablExceptionHandler {

    /**
     * 处理DB中字段唯一可能引发的异常
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> execeptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        if(ex.getMessage().contains("Duplicate entry")){
            String[] msgs = ex.getMessage().split(" ");
            String errName = msgs[2]+"已存在";
            return R.error(errName);
        }

        return R.error("未知错误");
    }

    /**
     * 处理分类被删除时可能引发的异常
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> customExceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }


}
