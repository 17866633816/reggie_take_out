package com.itheima.reggie.common;

/**
 * 此工具类用于公共字段填充
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void set(Long id){
        threadLocal.set(id);
    }

    public static Long get(){
        return threadLocal.get();
    }

}
