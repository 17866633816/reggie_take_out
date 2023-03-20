package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 向用户发送验证码
     * @param user
     * @param httpSession
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendSMS(@RequestBody User user, HttpSession httpSession){

        //取出手机号
        String phone = user.getPhone();
        log.info("手机号："+user.getPhone().toString());
        if (phone!=null){
            //生成随机验证码
            String randomCode = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info(randomCode);
            //给用户发送短信
            //SMSUtils.sendMessage("瑞吉外卖","45555",phone,randomCode);
            //将手机号跟验证码保存到session中
            httpSession.setAttribute(phone,randomCode);
            return R.success("短信发送成功");
        }

        return R.error("短信发送失败");
    }

    /**
     * 登录功能
     * @param map
     * @param httpSession
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession httpSession){
        //获取前端传来的手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //从session中取出发送给用户的验证码
        String code1 = httpSession.getAttribute(phone).toString();
        //比对验证码，如果相同则登录成功
        if (code1!=null && code1.equals(code)){
            //判断该手机号是否是新用户,如果不是，则将其添加到用户表中
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone,phone);
            User user = userService.getOne(lqw);
            if (user==null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //登陆成功，将用户添加到session中
            httpSession.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登陆失败");
    }


}
