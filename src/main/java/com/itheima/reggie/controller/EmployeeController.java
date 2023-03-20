package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 用户登录
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest httpServletRequest, @RequestBody Employee employee){
        //1.将请求中的密码取出进行加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据账号从数据库中查询数据
        LambdaQueryWrapper<Employee> employeeCondition = new LambdaQueryWrapper<Employee>();
        employeeCondition.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(employeeCondition);

        if (emp == null){
            return R.error("该账户不存在，请重新输入");
        }

        //3.将数据库中查询出的密码与用户输入的密码进行比对
        if (!emp.getPassword().equals(password)){
            return R.error("你输入的密码有误，请重新输入");
        }

        //4.查看账号的状态
        if (emp.getStatus() != 1){
            return R.error("该账户已被禁用，无法登陆");
        }

        //5.登录成功
        httpServletRequest.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 用户退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //将用户id从session中移除，代表用户退出登录
        request.getSession().removeAttribute("employee");
        //将退出成功的结果响应给前端
        return R.success("退出成功");
    }


    /**
     * 新增用户
     * @param request
     * @param employee
     */
    @PostMapping
    public R<String> save(HttpServletRequest request , @RequestBody Employee employee){

        //设置新增用户的密码默认为123456，并用md5算法进行加密
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

//        Long employeeId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(employeeId);
//        employee.setUpdateUser(employeeId);
        employeeService.save(employee);

        return R.success("新增员工成功");
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
        //1.创建分页构造器
        Page pageInfo = new Page(page,pageSize);

        //2.创建条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        lqw.orderByAsc(Employee::getName);

        //3.执行查询
        employeeService.page(pageInfo,lqw);

        return R.success(pageInfo);
    }


    /**
     * 根据id更新员工信息
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest httpServletRequest,@RequestBody Employee employee){
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((long)httpServletRequest.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }


    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id){
        log.info("员工id为:{}",id);
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }

}
