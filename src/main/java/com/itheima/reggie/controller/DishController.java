package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveDishAndFlavor(dishDto);
        return R.success("菜品保存成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> pageInfo1 = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null,Dish::getName,name);
        lqw.orderByDesc(Dish::getUpdateTime);
        //进行分页查询
        dishService.page(pageInfo, lqw);
        //除了records属性，将原Page对象中的属性值复制给新Page对象
        BeanUtils.copyProperties(pageInfo,pageInfo1,"records");
        //将分类名称查询出来赋给records属性
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> newRecords = records.stream().map((item) -> {
            DishDto newRecord = new DishDto();
            BeanUtils.copyProperties(item,newRecord);
            //根据分类id查询分类名称
            Long categoryId = item.getCategoryId();
            Category result = categoryService.getById(categoryId);
            String categoryName = result.getName();
            //将分类名称给到newRecords
            newRecord.setCategoryName(categoryName);
            return newRecord;
        }).collect(Collectors.toList());
        //更换Page对象中的recoeds属性
        pageInfo1.setRecords(newRecords);

        return R.success(pageInfo1);
    }

    /**
     * 根据id查询菜品、口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 更新菜品表和口味表
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("已更新");
    }

    /**
     * 根据分类id查询菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId()!= null,Dish::getCategoryId,dish.getCategoryId());
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //查询菜品
        List<Dish> dishes = dishService.list(lqw);
        //将Dish扩展为DishDto
        List<DishDto> dishDtos = dishes.stream().map((item) -> {
            DishDto dishdto = new DishDto();
            BeanUtils.copyProperties(item,dishdto);
            //根据菜品id查询口味，并将口味给到dishdto
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(lambdaQueryWrapper);
            dishdto.setFlavors(dishFlavors);
            return dishdto;
        }).collect(Collectors.toList());

        return R.success(dishDtos);
    }

}
