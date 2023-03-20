package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info(category.toString());
        categoryService.save(category);
        return R.success("分类添加成功");
    }


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //1.创建分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //2.创建条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        //根据soft字段升序
        lqw.orderByAsc(Category::getSort);
        //3.查询数据
        categoryService.page(pageInfo,lqw);

        return R.success(pageInfo);
    }


    /**
     * 按照id删除类别
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id){
        categoryService.remove(id);
        return R.success("类别删除成功");
    }


    /**
     * 根据id修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("分类修改成功");
    }

    /**
     * 根据前端传过来的type的值获取菜品分类,用于回显数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(category.getType()!=null,Category::getType,category.getType());
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getCreateTime);
        //从数据库中查询
        List<Category> categories = categoryService.list(lqw);
        //将数据返回给前端
        return R.success(categories);
    }
}
