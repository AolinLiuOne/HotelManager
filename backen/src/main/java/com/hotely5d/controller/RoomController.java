package com.hotely5d.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.entity.Category;
import com.hotely5d.entity.Hotel;
import com.hotely5d.entity.Room;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.entity.query.RoomQuery;
import com.hotely5d.service.AppointmentService;
import com.hotely5d.service.CategoryService;
import com.hotely5d.service.HotelService;
import com.hotely5d.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private HotelService hotelService;

    /**
    * 查询全部
    * @return
    */
    @GetMapping
    public Result findAll(@RequestParam("hotelId") Integer hotelId){
    List<Room> roomList = roomService.findAll(hotelId);
        return new Result(true, StatusCode.OK,"查询成功",roomList);
    }

    /**
    * 根据条件查询
    * @param room
    * @return
    */
    @GetMapping("search")
    public Result search(Room room){
        List<Room> roomList = roomService.search(room);
        return new Result(true, StatusCode.OK,"查询成功",roomList);
    }

    /**
    * 根据条件分页查询
    * @param room
    * @return
    */
    @GetMapping("search/{current}/{size}")
    public Result search(@PathVariable Integer current,@PathVariable Integer size ,Room room){
        Page<Room> page = roomService.search(new Page<Room>(current, size), room,room.getHotelId());
        return new Result(true, StatusCode.OK,"查询成功",page);
    }

    /**
    * 新增
    * @param room
    * @return
    */
    @PostMapping
    public Result add(@RequestBody Room room){
        return roomService.add(room);
    }

    /**
    * 修改
    * @param room
    * @return
    */
    @PutMapping
    public Result modify(@RequestBody Room room){
        return roomService.modify(room);
    }

    /**
    * 根据id查询
    * @param id
    * @return
    */
    @GetMapping("{id}")
    public Result findById(@PathVariable("id") Integer id){
        Room room  = roomService.findById(id);
        return new Result(true, StatusCode.OK,"查询成功",room);
    }

    /**
    * 根据id删除
    * @param id
    * @return
    */
    @DeleteMapping("{id}")
    public Result removeById(@PathVariable("id") Integer id){
        if(roomService.checkRoom(id)){
            return new Result(false,StatusCode.ERROR,"还有未处理的订单和预约");
        }
        roomService.removeById(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }

    /**
     * 查询房间列表、关联查询房型、房间是否可预约
     * @return
     */
    @GetMapping("findRoomList")
    public Result findRoomList(RoomQuery roomQuery){
        List<Room> list = appointmentService.findRoomList(roomQuery);
        List<Hotel> listH = hotelService.findAll();
        for (Room room : list) {
            for (Hotel hotel : listH) {
                if (room.getHotelId().equals(hotel.getHotelId())) {
                    room.setHotelName(hotel.getHotel());// 假设 Category 里字段叫 categoryName
                    break;
                }
            }
        }
        return new Result(true, StatusCode.OK,"查询成功",list);
    }

}