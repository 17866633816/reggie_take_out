package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Mapper.DishMapper;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional //涉及到了二个表里的数据，开启事务
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品、口味
     * @param dishDto
     */
    @Override
    public void saveDishAndFlavor(DishDto dishDto) {
        //将dishDto对象中的数据添加到dish表里
        this.save(dishDto);

        //将dishDto对象中的数据添加到flavor表里
        Long dtoId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dtoId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id查询菜品、口味
     * @param id
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {

        DishDto dishDto = new DishDto();

        //查询菜品
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish,dishDto);
        //查询口味
        //条件构造器
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(id!=null,DishFlavor::getDishId,id);
        List<DishFlavor> flavors = dishFlavorService.list(lqw);
        dishDto.setFlavors(flavors);

        return dishDto;

    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //1.更新菜品表里的数据
        this.updateById(dishDto);
        //2.更新口味表里的数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            dishFlavorService.updateById(flavor);
        }
    }
}
