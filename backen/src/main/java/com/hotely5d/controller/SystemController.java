package com.hotely5d.controller;

import com.hotely5d.entity.Admin;
import com.hotely5d.entity.Member;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.entity.query.LoginQuery;
import com.hotely5d.service.AdminService;
import com.hotely5d.service.HotelService;
import com.hotely5d.service.MemberService;
import com.hotely5d.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/system")
public class SystemController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private HotelService hotelService;

    /**
     * 登录
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody LoginQuery query){
        if(query.getRole() == 1){
            return adminService.login(query);
        }
        return memberService.login(query);
    }

    /**
     * 注册
     * @return
     */
    @PostMapping("register")
    public Result register(@RequestBody Member member){
        return memberService.register(member);
    }

    /**
     * 根据token获取用户信息
     * @param request
     * @return
     */
    @GetMapping("getUserInfoByToken")
    public Result getUserInfoByToken(HttpServletRequest request){
        Long userId = JWTUtils.getUserId(request);
        Long roleId = JWTUtils.getRole(request);
        if(roleId == 1){
            Admin admin = adminService.findById(userId.intValue());
            return new Result(true, StatusCode.OK,"获取成功",admin);
        }
        if(roleId == 2){
            Member member = memberService.findById(userId.intValue());
            return new Result(true, StatusCode.OK,"获取成功",member);
        }
        return new Result(true, StatusCode.ERROR,"获取用户信息失败");
    }

    @GetMapping("getHotel")
    public Result getAllHotel(){
        return new Result(true,StatusCode.OK,"获取成功",hotelService.findAll());
    }

    @PostMapping("adminRegister")
    public Result adminRegister(@RequestBody Admin admin){
        return adminService.register(admin);
    }


}
