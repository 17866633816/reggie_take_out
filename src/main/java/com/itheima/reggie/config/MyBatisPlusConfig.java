package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig{

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){

        //1.创建MP的拦截器对象
        MybatisPlusInterceptor mpi = new MybatisPlusInterceptor();
        //2.添加分页拦截器
        mpi.addInnerInterceptor(new PaginationInnerInterceptor());
        return mpi;

    }

}
