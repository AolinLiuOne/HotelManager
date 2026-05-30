package com.hotely5d.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.dao.MemberMapper;
import com.hotely5d.dao.OrdersMapper;
import com.hotely5d.dao.RoomMapper;
import com.hotely5d.entity.Member;
import com.hotely5d.entity.Orders;
import com.hotely5d.entity.Room;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.entity.query.OrderQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private RoomService roomService;
    @Autowired
    private MemberMapper memberMapper;

    public List<Orders> findAll(Integer hotelId) {
        return ordersMapper.selectList(new EntityWrapper<Orders>().eq("hotel_id",hotelId));
    }

    public Page<Orders> search(Page<Orders> page,Orders orders,Integer hotelId) {
        List<Orders> list = ordersMapper.selectPage(page, getEntityWrapper(orders).eq("hotel_id",hotelId));
        for (Orders o : list) {
            Room room = roomService.findById(o.getRoomId());
            Member member = memberMapper.selectById(o.getMemberId());
            o.setRoom(room);
            o.setMember(member);
        }
        return page.setRecords(list);
    }


    public Page<Orders> searchPersonOrder(Page<Orders> page,Orders orders) {
        List<Orders> list = ordersMapper.selectPage(page, getEntityWrapper(orders));
        for (Orders o : list) {
            Room room = roomService.findById(o.getRoomId());
            Member member = memberMapper.selectById(o.getMemberId());
            o.setRoom(room);
            o.setMember(member);
        }
        return page.setRecords(list);
    }

    public List<Orders> search(Orders orders,Integer hotelId) {
        return ordersMapper.selectList(getEntityWrapper(orders).eq("hotel_id",hotelId));
    }

    //条件构造器
    private EntityWrapper getEntityWrapper(Orders orders){
        EntityWrapper entityWrapper = new EntityWrapper();
        if(null != orders){
            if(!StringUtils.isEmpty(orders.getRemark())){
                entityWrapper.like("remark",String.valueOf(orders.getRemark()));
            }
            if (!StringUtils.isEmpty(orders.getMemberId())) {
                entityWrapper.eq("member_id", String.valueOf(orders.getMemberId()));
            }
            if (!StringUtils.isEmpty(orders.getStatus())) {
                entityWrapper.eq("status", String.valueOf(orders.getStatus()));
            }
            entityWrapper.orderBy("id",false);
        }
        return entityWrapper;
    }

    public void modify(Orders orders) {
        ordersMapper.updateById(orders);
    }

    public Result add(OrderQuery orderQuery) {
        Orders orders = new Orders();
        //判断用户是否存在，不存在则创建
        List<Member> memberList = memberMapper.selectList(new EntityWrapper().eq("idcard",orderQuery.getIdcard()));
        if (memberList.size() != 0){
            orders.setMemberId(memberList.get(0).getId());
        }else{
            Member member = new Member();
            member.setCreateTime(new Date());
            member.setGender(orderQuery.getGender());
            member.setIdcard(orderQuery.getIdcard());
            member.setName(orderQuery.getName());
            member.setPhone(orderQuery.getPhone());
            member.setUsername(orderQuery.getPhone());
            member.setPassword("123456");//初始化密码
            member.setHead("b11.jpg");
            memberMapper.insert(member);
            orders.setMemberId(member.getId());
        }
        Room room = roomService.findById(orderQuery.getRoomId());
        orders.setStartTime(new Date());
        orders.setDays(orderQuery.getDays());
        orders.setRoomId(orderQuery.getRoomId());
        orders.setStatus(2);
        orders.setMoney(room.getCategory().getPrice());
        orders.setHotelId(room.getHotelId());
        ordersMapper.insert(orders);
        return new Result(true, StatusCode.OK,"操作成功");
    }

    public Orders findById(Integer id, Integer hotelId) {
        EntityWrapper<Orders> wrapper = new EntityWrapper<>();
        wrapper.eq("id", id);
        if (hotelId != null) {
            wrapper.eq("hotel_id", hotelId);
        }

        List<Orders> list = ordersMapper.selectList(wrapper);
        if (list != null && !list.isEmpty()) {
            return list.get(0); // 返回第一条
        }
        return null;
    }

    public Orders onlyFindById(Integer id) {
        EntityWrapper<Orders> wrapper = new EntityWrapper<>();
        wrapper.eq("id", id);

        List<Orders> list = ordersMapper.selectList(wrapper);
        if (list != null && !list.isEmpty()) {
            return list.get(0); // 返回第一条
        }
        return null;
    }

    public void relocate(Orders order) {
        // 查出当前订单
        Orders currentOrder = ordersMapper.selectById(order.getId());
        if (currentOrder == null) {
            throw new RuntimeException("订单不存在");
        }

        // 新房间（目标房间）
        Room newRoom = roomService.findById(order.getRoomId());
        if (newRoom == null) {
            throw new RuntimeException("目标房间不存在");
        }

        // 原房间（用于结算已住部分金额）
        Room originalRoom = roomService.findById(currentOrder.getRoomId());
        if (originalRoom == null) {
            throw new RuntimeException("原房间信息不存在");
        }

        // 转为只含年月日的 LocalDate 进行比较（忽略时分秒）
        LocalDate today = LocalDate.now();
        if (currentOrder.getStartTime() == null) {
            throw new RuntimeException("订单开始时间为空");
        }
        LocalDate startDate = currentOrder.getStartTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        int totalDays = currentOrder.getDays() == null ? 0 : currentOrder.getDays();
        if (totalDays <= 0) {
            throw new RuntimeException("订单天数不合法");
        }

        LocalDate endDate = startDate.plusDays(totalDays); // 不含 endDate

        // 情况一：已入住且未退房（今天 >= startDate && today < endDate） -> 拆单并把原订单退掉
        if (( today.isAfter(startDate)) && today.isBefore(endDate)) {
            // 已住天数（若今天就是开始日期，至少算 1 天）
            long stayedDays = ChronoUnit.DAYS.between(startDate, today);
            if (stayedDays <= 0) stayedDays = 1;
            if (stayedDays > totalDays) stayedDays = totalDays; // 保险判断

            // 更新并退掉原订单（按原房价结算已住部分）
            BigDecimal origPrice = originalRoom.getCategory() != null && originalRoom.getCategory().getPrice() != null
                    ? originalRoom.getCategory().getPrice()
                    : BigDecimal.ZERO;
            currentOrder.setDays((int) stayedDays);
            currentOrder.setCheckOutTime(new Date()); // 精确到时分秒
            currentOrder.setMoney(origPrice.multiply(BigDecimal.valueOf(stayedDays)));
            currentOrder.setStatus(3); // 标记为已退房
            ordersMapper.updateById(currentOrder);

            // 新建订单：从今天开始、住剩余天数（按新房价计）
            int remainingDays = totalDays - (int) stayedDays;
            if (remainingDays > 0) {
                Orders newOrder = new Orders();
                newOrder.setMemberId(currentOrder.getMemberId());
                newOrder.setRoomId(newRoom.getId());
                newOrder.setStartTime(new Date()); // 新订单的开始时间设为当前时刻（或按需设为当天 00:00）
                newOrder.setDays(remainingDays);
                newOrder.setStatus(2); // 置为已入住（根据你系统的状态定义）
                newOrder.setHotelId(currentOrder.getHotelId());
                BigDecimal newPrice = newRoom.getCategory() != null && newRoom.getCategory().getPrice() != null
                        ? newRoom.getCategory().getPrice()
                        : BigDecimal.ZERO;
                newOrder.setMoney(newPrice.multiply(BigDecimal.valueOf(remainingDays)));
                ordersMapper.insert(newOrder);
            }

            // 情况二：尚未到入住日 -> 直接修改该订单的房间和金额（不拆单）
        } else if (today.isBefore(startDate) || today.isEqual(startDate)) {
            BigDecimal newPrice = newRoom.getCategory() != null && newRoom.getCategory().getPrice() != null
                    ? newRoom.getCategory().getPrice()
                    : BigDecimal.ZERO;
            currentOrder.setRoomId(newRoom.getId());
            currentOrder.setMoney(newPrice.multiply(BigDecimal.valueOf(totalDays)));
            ordersMapper.updateById(currentOrder);

            // 其它情况（已过退房日等）不允许换房
        } else {
            throw new RuntimeException("订单已过期或已退房，不能换房");
        }
    }



    public void removeById(Integer id) {
        ordersMapper.deleteById(id);
    }

}
