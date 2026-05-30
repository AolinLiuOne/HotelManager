package com.hotely5d.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.dao.*;
import com.hotely5d.entity.*;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.entity.query.RoomQuery;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private CategoryService categoryService;

    public List<Appointment> findAll(Integer hotelId) {
        return appointmentMapper.selectList(new EntityWrapper<Appointment>().eq("hotel_id",hotelId));
    }

    public Page<Appointment> search(Page<Appointment> page, Appointment appointment) {
        List<Appointment> list = appointmentMapper.selectPage(page, getEntityWrapper(appointment));
        for (Appointment a : list) {
            Room room = roomService.findById(a.getRoomId());
            a.setRoom(room);
            Member member = memberMapper.selectById(a.getMemberId());
            a.setMember(member);
        }
        return page.setRecords(list);
    }

    public List<Appointment> search(Appointment appointment) {
        return appointmentMapper.selectList(getEntityWrapper(appointment));
    }

    //条件构造器
    private EntityWrapper getEntityWrapper(Appointment appointment) {
        EntityWrapper entityWrapper = new EntityWrapper();
        if (null != appointment) {
            if (!StringUtils.isEmpty(appointment.getRemark())) {
                entityWrapper.like("remark", String.valueOf(appointment.getRemark()));
            }
            if (!StringUtils.isEmpty(appointment.getMemberId())) {
                entityWrapper.eq("member_id", String.valueOf(appointment.getMemberId()));
            }
            if (!StringUtils.isEmpty(appointment.getStatus())) {
                entityWrapper.eq("status", String.valueOf(appointment.getStatus()));
            }
            if (!StringUtils.isEmpty(appointment.getHotelId())) {
                entityWrapper.eq("hotel_id", String.valueOf(appointment.getHotelId()));
            }
            entityWrapper.orderBy("id",false);
        }
        return entityWrapper;
    }

    public void modify(Appointment appointment) {
        appointmentMapper.updateById(appointment);
    }

    public Result add(Appointment appointment) {
        //判断是否有待确定的单
        Integer count = appointmentMapper.selectCount(new EntityWrapper()
                .eq("member_id", appointment.getMemberId())
                .eq("room_id", appointment.getRoomId())
                .eq("status", 1)
                .eq("hotel_id", appointment.getHotelId())
        );
        if(count != 0){
            return new Result(false, StatusCode.ERROR, "请勿重复预订！");
        }
        //判断是否已预约或者已入住这个房间
//        ArrayList<Integer> statusList = new ArrayList<>();
//        statusList.add(1);//已预订
//        statusList.add(2);//已入住
//        List<Orders> ordersList = ordersMapper.selectList(new EntityWrapper()
//                .eq("room_id", appointment.getRoomId())
//                .in("status", statusList));
//        if(ordersList.size() != 0){
//            return new Result(false, StatusCode.ERROR, "预订失败,房间已满！");
//        }
        //插入预约表
        Room r = roomService.findById(appointment.getRoomId());
        appointment.setMoney(r.getCategory().getPrice());
        appointment.setStatus(1);//1-待确认 2-预约成功 3-已取消
        appointment.setHotelId(r.getHotelId());
        appointmentMapper.insert(appointment);
        return new Result(true, StatusCode.OK, "操作成功,等待酒店确认！");
    }

    public Appointment findById(Integer id, Integer hotelId) {
        Appointment appointment = appointmentMapper.selectById(id);
        if(appointment.getHotelId().equals(hotelId)){
            return appointment;
        }
        return null;
    }

    public void removeById(Integer id) {
        appointmentMapper.deleteById(id);

    }

    public List<Room> findRoomList(RoomQuery roomQuery) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("status",1);
        if(!StringUtils.isEmpty(roomQuery.getCategoryId())){
            wrapper.eq("category_id",roomQuery.getCategoryId());
        }
        if(!StringUtils.isEmpty(roomQuery.getHotelId())){
            wrapper.eq("hotel_id",roomQuery.getHotelId());
        }
        List<Room> list = roomMapper.selectList(wrapper);
        for (Room r : list) {
            //查询房型
            Category category = categoryService.findById(r.getCategoryId());
            r.setCategory(category);
            //1.根据当前年月日和房间id判断是否入住，入住则不可预约和入住 2.根据当前年月日和房间id判断是否预约，预约成功则不可预约和入住
            ArrayList<Integer> statusList = new ArrayList<>();
            statusList.add(1);//已预订
            statusList.add(2);//已入住
            List<Orders> ordersList = ordersMapper.selectList(new EntityWrapper()
                    .eq("room_id", r.getId())
                    .in("status", statusList));
            if(ordersList.size() == 0){
                r.setCanUse(true);
                continue;
            }
            String startDate = DateFormatUtils.format(ordersList.get(0).getStartTime(),"yyyy-MM-dd");
            //不为0且当前日期!=入住日期，判断当前日期是否在入住日期+居住天数之后
            r.setCanUse(canAppointment(roomQuery.getStartDate(),startDate,ordersList.get(0).getDays()));
        }
        //过滤
        if(!StringUtils.isEmpty(roomQuery.getCanUse())){
            List<Room> rooms = list.stream().filter(f -> f.getCanUse() == roomQuery.getCanUse()).collect(Collectors.toList());
            return rooms;
        }
        return list;
    }

    //判断是否可预约
    public boolean canAppointment(String currentDate,String startDate,Integer days)  {
        List<String> dateList = new ArrayList();
        for (int i = 0; i < days ; i++) {
            try {
                Date date = DateUtils.parseDate(startDate, "yyyy-MM-dd");
                Date rs = DateUtils.addDays(date, i);
                dateList.add(DateFormatUtils.format(rs,"yyyy-MM-dd"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return !dateList.contains(currentDate);
    }

    public Result confirm(Integer appointmentId,Integer hotelId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        //判断是否有入住但是没退房或者有预订但是没入住
        ArrayList<Integer> statusList = new ArrayList<>();
        statusList.add(1);//已预订
        statusList.add(2);//已入住
        List<Orders> ordersList = ordersMapper.selectList(new EntityWrapper()
                .eq("room_id", appointment.getRoomId())
                .in("status", statusList));
        if(ordersList.size() > 0){
            return new Result(false, StatusCode.ERROR,"该房间已被占用，请取消预约或者前往【入住管理】退房！");
        }
        //修改预订状态，预订成功
        appointment.setStatus(2);
        appointmentMapper.updateById(appointment);
        //生成入住订单，状态为已预订
        Orders order = new Orders();
        order.setMemberId(appointment.getMemberId());
        order.setRoomId(appointment.getRoomId());
        order.setDays(appointment.getDays());
        order.setStartTime(appointment.getStartTime());
        order.setMoney(appointment.getMoney());
        order.setStatus(1);
        order.setHotelId(hotelId);
        ordersMapper.insert(order);
        return new Result(true, StatusCode.OK,"操作成功！");
    }

}
