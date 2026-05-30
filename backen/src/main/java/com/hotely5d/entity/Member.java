package com.hotely5d.entity;
import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;

/**
* Member实体类
*/
@TableName("member")
public class Member {
    @TableId("id")
    private Integer id;// ID
	@TableField("username")
    private String username;// 账号
	@TableField("password")
    private String password;// 密码
	@TableField("name")
    private String name;// 姓名
	@TableField("gender")
    private Integer gender;// 性别
	@TableField("phone")
    private String phone;// 手机
	@TableField("idcard")
    private String idcard;// 身份证
	@TableField("head")
    private String head;// 头像
	@TableField("create_time")
    private Date createTime;// 注册时间

    public Member() {
        super();
    }

    public  Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public  String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public  String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public  String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public  Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }
    public  String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public  String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }
    public  String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
    public  Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}