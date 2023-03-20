package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Long id = BaseContext.get();
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("createUser",id);
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",id);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long id = BaseContext.get();
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",id);
    }
}
