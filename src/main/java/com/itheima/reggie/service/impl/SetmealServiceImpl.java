package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.SetmealMapper;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 将数据保存到套餐表、套餐跟菜品的关系表
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存数据到套餐表
        setmealService.save(setmealDto);
        //保存数据到关系表
        Long id = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> collect = setmealDishes.stream().map((item) -> {
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(collect);
    }

    /**
     * 根据套餐id删除套餐数据、套餐菜品关系表中的数据
     * @param ids
     */
    @Override
    @Transactional
    public void deleteWithDish(List<Long> ids) {
        //判断套餐是否处于启售状态
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.in(Setmeal::getId,ids);
        lqw.eq(Setmeal::getStatus,1);
        int count = this.count(lqw);
        if ( count>0 ){
            throw new CustomException("有套餐处于起售状态，无法删除");
        }
        //删除套餐表中的数据
        this.removeByIds(ids);
        //删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> lqw1 = new LambdaQueryWrapper<>();
        lqw1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lqw1);
    }
}
