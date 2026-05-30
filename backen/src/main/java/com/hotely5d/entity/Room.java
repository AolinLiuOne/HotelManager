package com.hotely5d.entity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;

/**
* Room实体类
*/
@TableName("room")
public class Room {
    @TableId("id")
    private Integer id;// ID
	@TableField("room_num")
    private Integer roomNum;// 房间号
	@TableField("category_id")
    private Integer categoryId;// 房型
	@TableField("status")
    private Integer status;// 状态 1-正常 2-停用
    @TableField("hotel_id")
    private Integer hotelId;


    @TableField(exist = false)
    private Category category = new Category();

    @TableField(exist = false)
    private Boolean canUse; //是否可用(可预约或者可入住)

    @TableField(exist = false)
    private List<String> notUseDateList = new ArrayList(); //当前已被预订或入住的日期

    @TableField(exist = false)  // MyBatis-Plus: 表中不存在这个字段
    private String hotelName;

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public Room() {
        super();
    }

    public  Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public  Integer getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(Integer roomNum) {
        this.roomNum = roomNum;
    }
    public  Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    public  Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getCanUse() {
        return canUse;
    }

    public void setCanUse(Boolean canUse) {
        this.canUse = canUse;
    }

    public List<String> getNotUseDateList() {
        return notUseDateList;
    }

    public void setNotUseDateList(List<String> notUseDateList) {
        this.notUseDateList = notUseDateList;
    }

    public Integer getHotelId() {
        return hotelId;
    }

    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }
}