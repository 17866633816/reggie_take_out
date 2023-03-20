package com.itheima.reggie.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
