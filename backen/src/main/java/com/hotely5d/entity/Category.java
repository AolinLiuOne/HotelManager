package com.hotely5d.entity;
import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;

/**
* Category实体类
*/
@TableName("category")
public class Category {
    @TableId("id")
    private Integer id;// ID
	@TableField("category_name")
    private String categoryName;// 房间类型
	@TableField("photo")
    private String photo;// 房间图片
	@TableField("price")
    private BigDecimal price;// 价格
	@TableField("live_num")
    private Integer liveNum;// 可住人数
	@TableField("bed_num")
    private Integer bedNum;// 床位数
	@TableField("area")
    private String area;// 面积
	@TableField("introduce")
    private String introduce;// 介绍
    @TableField("hotel_id")
    private Integer hotelId;

    @TableField(exist = false)
    private Integer roomNum = 0; //房间数

    public Category() {
        super();
    }

    public  Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public  String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public  String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public  BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public  Integer getLiveNum() {
        return liveNum;
    }

    public void setLiveNum(Integer liveNum) {
        this.liveNum = liveNum;
    }
    public  Integer getBedNum() {
        return bedNum;
    }

    public void setBedNum(Integer bedNum) {
        this.bedNum = bedNum;
    }
    public  String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
    public  String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Integer getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(Integer roomNum) {
        this.roomNum = roomNum;
    }

    public Integer getHotelId() {
        return hotelId;
    }

    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }
}