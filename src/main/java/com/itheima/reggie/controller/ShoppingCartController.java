package com.itheima.reggie.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 向购物车中添加菜品或套餐
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("添加到购物车表的数据：{}",shoppingCart);
        //获取当前用户id并将其添加到购物车对象中
        Long userId = BaseContext.get();
        shoppingCart.setUserId(userId);
        //判断当前用户对应的购物车中是否已经存在要添加的菜品或套餐
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,userId);
        Long dishId = shoppingCart.getDishId();

        if (dishId != null){
            //添加的菜品
            lqw.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else{
            //添加的套餐
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart ds = shoppingCartService.getOne(lqw);

        if (ds != null){
            //购物车已有此菜品或套餐
            Integer number = ds.getNumber();
            ds.setNumber(number+1);
            shoppingCartService.updateById(ds);
        }else{
            //购物车无此菜品或套餐
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            ds = shoppingCart;
        }

        return R.success(ds);

    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/show")
    public R<List<ShoppingCart>> showShoppingCart(){
        //获取当前用户的ID
        Long userId = BaseContext.get();
        //根据用户id查询购物车中的菜品和套餐
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> list = shoppingCartService.list(lqw);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> delete(){
        //获取当前用户的ID
        Long userId = BaseContext.get();
        //根据用户id删除购物车中的菜品和套餐
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(lqw);

        return  R.success("清空购物车成功");
    }

}
