package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order){
        log.info("前端传来的订单数据为:{}",order);

        ordersService.submit(order);

        return R.success("下单成功");
    }
}
