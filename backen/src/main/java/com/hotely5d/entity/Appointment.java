package com.hotely5d.entity;
import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;
import org.springframework.format.annotation.DateTimeFormat;

/**
* Appointment实体类
*/
@TableName("appointment")
public class Appointment {
    @TableId("id")
    private Integer id;// ID
	@TableField("member_id")
    private Integer memberId;// 用户
	@TableField("room_id")
    private Integer roomId;// 房型
	@TableField("start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;// 入住时间
	@TableField("days")
    private Integer days;// 居住天数
	@TableField("status")
    private Integer status;// 状态 1-待确认 2-预约成功 3-预约失败
	@TableField("remark")
    private String remark;// 备注
	@TableField("money")
    private BigDecimal money;// 已付金额
    @TableField("hotel_id")
    private Integer hotelId;


    @TableField(exist = false)
    private Room room = new Room();
    @TableField(exist = false)
    private Member member = new Member();

    public Appointment() {
        super();
    }



    public  Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public  Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public  Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public  Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public  String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public  BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }


    public Integer getHotelId() {
        return hotelId;
    }

    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }
}