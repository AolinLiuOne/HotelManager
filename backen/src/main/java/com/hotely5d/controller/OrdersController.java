package com.hotely5d.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.entity.Orders;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.entity.query.OrderQuery;
import com.hotely5d.service.OrdersService;
import com.hotely5d.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
    * 查询全部
    * @return
    */
    @GetMapping
    public Result findAll(@RequestParam("hotelId") Integer hotelId){
    List<Orders> ordersList = ordersService.findAll(hotelId);
        return new Result(true, StatusCode.OK,"查询成功",ordersList);
    }

    /**
    * 根据条件查询
    * @param orders
    * @return
    */
    @GetMapping("search")
    public Result search(Orders orders){
        List<Orders> ordersList = ordersService.search(orders,orders.getHotelId());
        return new Result(true, StatusCode.OK,"查询成功",ordersList);
    }

    /**
    * 根据条件分页查询
    * @param orders
    * @return
    */
    @GetMapping("search/{current}/{size}")
    public Result search(@PathVariable Integer current, @PathVariable Integer size , Orders orders, HttpServletRequest request){
        Long roleId = JWTUtils.getRole(request);
        if(roleId == 2){//用户只能看自己的
            Long userId = JWTUtils.getUserId(request);
            orders.setMemberId(userId.intValue());
        }
        Page<Orders> page = ordersService.search(new Page<Orders>(current, size), orders, orders.getHotelId());
        return new Result(true, StatusCode.OK,"查询成功",page);
    }

    @GetMapping("searchPerson/{current}/{size}")
    public Result searchPerson(@PathVariable Integer current, @PathVariable Integer size , Orders orders, HttpServletRequest request){
        Long roleId = JWTUtils.getRole(request);
        if(roleId == 2){//用户只能看自己的
            Long userId = JWTUtils.getUserId(request);
            orders.setMemberId(userId.intValue());
        }
        Page<Orders> page = ordersService.searchPersonOrder(new Page<Orders>(current, size), orders);
        return new Result(true, StatusCode.OK,"查询成功",page);
    }

    /**
    * 后台-新增入住订单
    * @param orderQuery
    * @return
    */
    @PostMapping
    public Result add(@RequestBody OrderQuery orderQuery){
        return ordersService.add(orderQuery);
    }

    /**
    * 修改
    * @param orders
    * @return
    */
    @PutMapping
    public Result modify(@RequestBody Orders orders){
        ordersService.modify(orders);
        return new Result(true, StatusCode.OK,"修改成功");
    }


    @PutMapping("relocate")
    public Result relocate(@RequestBody Orders orders){
        ordersService.relocate(orders);
        return new Result(true,StatusCode.OK,"换房成功");
    }

    /**
    * 根据id查询
    * @param id
    * @return
    */
    @GetMapping("{id}")
    public Result findById(@PathVariable("id") Integer id,@RequestParam("hotelId") Integer hotelId){
        Orders orders  = ordersService.findById(id,hotelId);
        return new Result(true, StatusCode.OK,"查询成功",orders);
    }

    /**
    * 根据id删除
    * @param id
    * @return
    */
    @DeleteMapping("{id}")
    public Result removeById(@PathVariable("id") Integer id){

        ordersService.removeById(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }

    /**
     * 后台-确定入住
     * @param id
     * @return
     */
    @GetMapping("confirm/{id}")
    public Result confirm(@PathVariable("id") Integer id){
        Orders orders = ordersService.onlyFindById(id);
        orders.setStatus(2);
        ordersService.modify(orders);
        return new Result(true, StatusCode.OK,"操作成功");
    }

    /**
     * 后台-退房
     * @return
     */
    @GetMapping("checkOut")
    public Result confirm(Integer id,String remark,Integer hotelId){
        Orders orders = ordersService.findById(id,hotelId);
        orders.setRemark(remark);
        orders.setStatus(3);
        ordersService.modify(orders);
        return new Result(true, StatusCode.OK,"操作成功");
    }

}