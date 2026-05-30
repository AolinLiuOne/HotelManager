package com.hotely5d.entity;
import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;

/**
* Orders实体类
*/
@TableName("orders")
public class Orders {
    @TableId("id")
    private Integer id;// ID
	@TableField("member_id")
    private Integer memberId;// 用户
	@TableField("room_id")
    private Integer roomId;// 房间
	@TableField("start_time")
    private Date startTime;// 入住开始时间
	@TableField("days")
    private Integer days;// 居住天数
	@TableField("status")
    private Integer status;// 状态 (1-已预订 2-已入住 3-已退房)
	@TableField("remark")
    private String remark;// 备注
	@TableField("money")
    private BigDecimal money;// 已付金额
    @TableField("check_out_time")
    private Date checkOutTime;// 退房时间

    @TableField(exist = false)
    private Room room = new Room();
    @TableField(exist = false)
    private Member member = new Member();

    @TableField("hotel_id")
    private Integer hotelId;

    public Orders() {
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
    public  Integer getRoomId() {
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

    public Date getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Date checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Integer getHotelId() {
        return hotelId;
    }

    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }
}