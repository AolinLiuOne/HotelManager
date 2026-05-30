package com.hotely5d.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.entity.Appointment;
import com.hotely5d.entity.Room;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.entity.query.RoomQuery;
import com.hotely5d.service.AppointmentService;
import com.hotely5d.service.OrdersService;
import com.hotely5d.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private OrdersService ordersService;

    /**
    * 根据条件查询
    * @param appointment
    * @return
    */
    @GetMapping("search")
    public Result search(Appointment appointment){
        List<Appointment> appointmentList = appointmentService.search(appointment);
        return new Result(true, StatusCode.OK,"查询成功",appointmentList);
    }

    /**
    * 根据条件分页查询预订
    * @param appointment
    * @return
    */
    @GetMapping("search/{current}/{size}")
    public Result search(@PathVariable Integer current,@PathVariable Integer size ,Appointment appointment,HttpServletRequest request){
        Long roleId = JWTUtils.getRole(request);
        if(roleId == 2){//用户只能看自己的
            Long userId = JWTUtils.getUserId(request);
            appointment.setMemberId(userId.intValue());
        }
        Page<Appointment> page = appointmentService.search(new Page<Appointment>(current, size), appointment);
        return new Result(true, StatusCode.OK,"查询成功",page);
    }

    /**
     * 后台-确认用户预订
     * @param appointmentId
     * @return
     */
    @GetMapping("confirm")
    public Result confirm(Integer appointmentId,Integer hotelId){
        return appointmentService.confirm(appointmentId,hotelId);
    }

    /**
     * 取消预订
     * @param appointment
     * @return
     */
    @PostMapping("cancel")
    public Result cancel(@RequestBody Appointment appointment, HttpServletRequest request){
        if(null == appointment.getMemberId()){//用户取消
            Long userId = JWTUtils.getUserId(request);
            appointment.setMemberId(userId.intValue());
        }
        appointment.setStatus(3);
        appointmentService.modify(appointment);
        return new Result(true, StatusCode.OK,"操作成功！");
    }

    /**
    * 前台-用户预订
    * @param appointment
    * @return
    */
    @PostMapping
    public Result add(@RequestBody Appointment appointment, HttpServletRequest request){
        Long userId = JWTUtils.getUserId(request);
        appointment.setMemberId(userId.intValue());
        return appointmentService.add(appointment);
    }

    /**
    * 根据id查询
    * @param id
    * @return
    */
    @GetMapping("{id}")
    public Result findById(@PathVariable("id") Integer id,
                           @RequestParam("hotelId") Integer hotelId) {
        Appointment appointment = appointmentService.findById(id, hotelId);
        return new Result(true, StatusCode.OK, "查询成功", appointment);
    }


    /**
    * 根据id删除
    * @param id
    * @return
    */
    @DeleteMapping("{id}")
    public Result removeById(@PathVariable("id") Integer id) {
        appointmentService.removeById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }




}