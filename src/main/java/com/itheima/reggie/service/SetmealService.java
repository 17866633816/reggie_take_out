package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 将数据保存到套餐表、套餐跟菜品的关系表
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 根据套餐id删除套餐数据、套餐菜品关系表中的数据
     * @param ids
     */
    void deleteWithDish(List<Long> ids);
}
