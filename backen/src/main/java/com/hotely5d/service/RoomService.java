package com.hotely5d.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.dao.AppointmentMapper;
import com.hotely5d.dao.CategoryMapper;
import com.hotely5d.dao.OrdersMapper;
import com.hotely5d.dao.RoomMapper;
import com.hotely5d.entity.Appointment;
import com.hotely5d.entity.Category;
import com.hotely5d.entity.Orders;
import com.hotely5d.entity.Room;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.*;

@Service
@Transactional
public class RoomService {

    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private AppointmentMapper appointmentMapper;

    public List<Room> findAll(Integer hotelId) {
        return roomMapper.selectList(new EntityWrapper<Room>().eq("hotel_id", hotelId));
    }

    public Page<Room> search(Page<Room> page,Room room,Integer hotelId) {
        List<Room> list = roomMapper.selectPage(page, getEntityWrapper(room).eq("hotel_id", hotelId));
        for (Room r : list) {
            Category category = categoryMapper.selectById(r.getCategoryId());
            r.setCategory(category);
        }
        return page.setRecords(list);
    }

    public List<Room> search(Room room) {
        return roomMapper.selectList(getEntityWrapper(room));
    }

    //条件构造器
    private EntityWrapper getEntityWrapper(Room room){
        EntityWrapper entityWrapper = new EntityWrapper();
        if(null != room){
            if(!StringUtils.isEmpty(room.getRoomNum())){
                entityWrapper.like("room_num",String.valueOf(room.getRoomNum()));
            }
            if(!StringUtils.isEmpty(room.getCategoryId())){
                entityWrapper.eq("category_id", room.getCategoryId());
            }
            if(!StringUtils.isEmpty(room.getStatus())){
                entityWrapper.eq("status", room.getStatus());
            }
        }
        return entityWrapper;
    }

    public Result modify(Room room) {
        Integer count = roomMapper.selectCount(
                new EntityWrapper()
                        .eq("room_num", room.getRoomNum())
                        .eq("hotel_id",room.getHotelId())
                        .notIn("id", room.getId()));
        if(count != 0){
            return new Result(false, StatusCode.ERROR,"房间号已存在");
        }
        roomMapper.updateById(room);
        return new Result(true, StatusCode.OK,"修改成功");
    }

    public Result add(Room room) {
        Integer count = roomMapper.selectCount(new EntityWrapper().eq("room_num", room.getRoomNum()));
        if(count != 0){
            return new Result(false, StatusCode.ERROR,"房间号已存在");
        }
        roomMapper.insert(room);
        return new Result(true, StatusCode.OK,"新增成功");
    }

    public Room findById(Integer id) {
        Room room = roomMapper.selectById(id);
        //1.查询房型

        Category category = categoryMapper.selectById(room.getCategoryId());
        room.setCategory(category);
        //2.查询今日是否可(预订/入住)
        ArrayList<Integer> statusList = new ArrayList<>();
        statusList.add(1);//已预订
        statusList.add(2);//已入住
        List<Orders> ordersList = ordersMapper.selectList(new EntityWrapper()
                .eq("room_id", room.getId())
                .in("status", statusList));
        if(ordersList.size() == 0){
            room.setCanUse(true);
            return room;
        }

        String startDate = DateFormatUtils.format(ordersList.get(0).getStartTime(),"yyyy-MM-dd");
        String currDate = DateFormatUtils.format(new Date(),"yyyy-MM-dd");
        //不为0，判断当前日期是否在入住日期+居住天数之后
        room.setCanUse(appointmentService.canAppointment(currDate,startDate,ordersList.get(0).getDays()));
        //3.查询已被(预订/入住)的日期
        List<String> dateList = new ArrayList();
        for (int i = 0; i < ordersList.get(0).getDays() ; i++) {
            try {
                Date date = DateUtils.parseDate(startDate, "yyyy-MM-dd");
                Date rs = DateUtils.addDays(date, i);
                dateList.add(DateFormatUtils.format(rs,"yyyy-MM-dd"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        room.setNotUseDateList(dateList);
        return room;
    }

    public void removeById(Integer id) {
        roomMapper.deleteById(id);
    }

    /**
     * 房型id
     * @param id
     * @return
     */

    public boolean findRoom(Integer id,Integer hotelId) {
        List<Room> roomList = roomMapper.selectList(new EntityWrapper().eq("hotel_id", hotelId).eq("category_id", id));
        return roomList.isEmpty();
    }

    public boolean checkRoom(Integer id) {
        List<Orders> orderList = ordersMapper.selectList(new EntityWrapper().eq("room_id", id));
        List<Appointment> appointmentList = appointmentMapper.selectList(new EntityWrapper().eq("room_id", id));
        for (Orders order : orderList) {
            if (order.getStatus() == 1 || order.getStatus() == 2) {
                return true;
            }
        }
        for (Appointment appointment : appointmentList) {
            if (appointment.getStatus() == 1 || appointment.getStatus() == 2) {
                return true;
            }
        }
        return false;

    }
}
