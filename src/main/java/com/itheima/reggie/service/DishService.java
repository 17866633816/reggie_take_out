package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

     /**
      * 保存菜品表和口味表
      * @param dishDto
      */
     void saveDishAndFlavor(DishDto dishDto);

     /**
      * 根据id从菜品表和口味表里查询数据
      * @param id
      * @return
      */
     DishDto getByIdWithFlavor(Long id);

     /**
      * 更新菜品表和口味表
      * @param dishDto
      */
     void updateWithFlavor(DishDto dishDto);

}
