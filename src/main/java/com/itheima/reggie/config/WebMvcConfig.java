package com.itheima.reggie.config;

import com.itheima.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;


@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {

    //设置静态资源的放行
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("front/**").addResourceLocations("classpath:/front/");
    }

    //扩展SpringMVC的消息转换器
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //1.创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //2.向消息转换器对象中添加对象转换器
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //3.将消息转换器对象添加到MVC消息转换器的集合中
        converters.add(0,messageConverter); // 索引为0代表将自己添加的转换器置于第一位
    }
}
