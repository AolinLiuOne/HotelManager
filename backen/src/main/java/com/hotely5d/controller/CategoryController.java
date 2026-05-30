package com.hotely5d.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.entity.Category;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.service.AppointmentService;
import com.hotely5d.service.CategoryService;
import com.hotely5d.service.OrdersService;
import com.hotely5d.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private OrdersService ordersService;

    /**
    * 查询全部
    * @return
    */
    @GetMapping
    public Result findAll(Integer hotelId) {
    List<Category> categoryList = categoryService.findAll(hotelId);
        return new Result(true, StatusCode.OK,"查询成功",categoryList);
    }
    @GetMapping("searchAll")
    public Result searchAll() {
        return new Result(true,StatusCode.OK,"查询成功",categoryService.searchAll());
    }

    /**
    * 根据条件查询
    * @param category
    * @return
    */
    @GetMapping("search")
    public Result search(Category category){
        List<Category> categoryList = categoryService.search(category,category.getHotelId());
        return new Result(true, StatusCode.OK,"查询成功",categoryList);
    }

    /**
    * 根据条件分页查询
    * @param category
    * @return
    */
    @GetMapping("search/{current}/{size}")
    public Result search(@PathVariable Integer current,@PathVariable Integer size ,Category category){
        Page<Category> page = categoryService.search(new Page<Category>(current, size), category, category.getHotelId());
        return new Result(true, StatusCode.OK,"查询成功",page);
    }

    /**
    * 新增
    * @param category
    * @return
    */
    @PostMapping
    public Result add(@RequestBody Category category){
        categoryService.add(category);
        return new Result(true, StatusCode.OK,"新增成功");
    }

    /**
    * 修改
    * @param category
    * @return
     *
     * 传过来的要有hotelId
    */
    @PutMapping
    public Result modify(@RequestBody Category category){
        categoryService.modify(category);
        return new Result(true, StatusCode.OK,"修改成功");
    }

    /**
    * 根据id查询
    * @param id
    * @return
    */
    @GetMapping("{id}")
    public Result findById(@PathVariable("id") Integer id){
        Category category  = categoryService.findById(id);
        return new Result(true, StatusCode.OK,"查询成功",category);
    }

    @GetMapping("{hotelId}")
    public Result findByHotelId(@PathVariable("hotelId") Integer hotelId){
        List<Category> category = categoryService.findAll(hotelId);
        return new Result(true,StatusCode.OK,"查询成功",category);
    }

    /**
    * 根据id删除
    * @param id
    * @return
    */
    @DeleteMapping("{id}")
    public Result removeById(@PathVariable("id") Integer id,@RequestParam("hotelId") Integer hotelId){
        if(!roomService.findRoom(id,hotelId)){
            return new Result(true,StatusCode.ERROR,"存在房间未删除");
        }
        categoryService.removeById(id,hotelId);
        return new Result(true, StatusCode.OK,"删除成功");
    }

}