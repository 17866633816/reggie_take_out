package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.CategoryMapper;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    @Override
    public void remove(long id) {
        //1.查看是否有菜品在这个分类里，没有则可以删除这个分类
        LambdaQueryWrapper<Dish> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(lqw1);
        if (count1>0){
            //此分类不可删除
            throw new CustomException("还有菜品在此分类中，此分类不可删除");
        }

        //2.查看是否有套餐在这个分类里，没有则可以删除这个分类
        LambdaQueryWrapper<Setmeal> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(lqw2);
        if (count2>0){
            //此分类不可删除
            throw new CustomException("还有套餐在此分类中，此分类不可删除");
        }

        removeById(id);
    }
}
