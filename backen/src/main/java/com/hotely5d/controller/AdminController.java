package com.hotely5d.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.entity.Admin;
import com.hotely5d.entity.Hotel;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.entity.query.LoginQuery;
import com.hotely5d.service.AdminService;
import com.hotely5d.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private HotelService hotelService;


    /**
    * 根据条件分页查询
    * @param admin
    * @return
    */
    @GetMapping("search/{current}/{size}")
    public Result search(@PathVariable Integer current,@PathVariable Integer size ,Admin admin){
        Page<Admin> page = adminService.search(new Page<Admin>(current, size), admin);
        return new Result(true, StatusCode.OK,"查询成功",page);
    }

    /**
    * 新增
    * @param admin
    * @return
    */
    @PostMapping
    public Result add(@RequestBody Admin admin){
        adminService.add(admin);
        return new Result(true, StatusCode.OK,"新增成功");
    }

    /**
    * 修改
    * @param admin
    * @return
    */
    @PutMapping
    public Result modify(@RequestBody Admin admin){
        adminService.modify(admin);
        return new Result(true, StatusCode.OK,"修改成功");
    }

    /**
    * 根据id删除
    * @param id
    * @return
    */
    @DeleteMapping("{id}")
    public Result removeById(@PathVariable("id") Integer id){
        adminService.removeById(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }

    @PostMapping("addHotel")
    public Result addHotel(@RequestBody Hotel hotel){
        List<Hotel> temp = hotelService.selectByHotelName(hotel.getHotel());
        if(!temp.isEmpty()){
            return new Result(false,StatusCode.ERROR,"已存在该酒店");
        }
        hotelService.add(hotel);
        return new Result(true,StatusCode.OK,"注册酒店成功");
    }

    @GetMapping("/{hotelId}")
    public Result getHotelNameById(@PathVariable Integer hotelId) {
        // 调用 service 查询数据库
        Hotel hotel = hotelService.getById(hotelId);
        if (hotel == null) {
            throw new RuntimeException("酒店不存在");
        }
        return new Result(true,StatusCode.OK,"获得酒店名称",hotel);
    }

}