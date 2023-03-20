package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 将数据保存到套餐表、套餐跟菜品的关系表
     * @param setmealDto
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("新增成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page,int pageSize ,String name){
        log.info("参数值"+page+pageSize);
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> pageInfo1 = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(name!=null,Setmeal::getName,name);
        lqw.orderByAsc(Setmeal::getUpdateTime);
        //分页查询
        setmealService.page(pageInfo,lqw);

        BeanUtils.copyProperties(pageInfo,pageInfo1,"records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> collect = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            //根据分类id获取分类名称
            Long categoryId = item.getCategoryId();
            String categoryName = categoryService.getById(categoryId).getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());

        pageInfo1.setRecords(collect);

        //将结果返回给前端
        return R.success(pageInfo1);
    }

    /**
     * 根据id删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info(ids.toString());
        setmealService.deleteWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        //条件构造器
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lqw.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        lqw.orderByDesc(Setmeal::getUpdateTime);
        //查询套餐
        List<Setmeal> setmeals = setmealService.list(lqw);

        return R.success(setmeals);
    }
}
