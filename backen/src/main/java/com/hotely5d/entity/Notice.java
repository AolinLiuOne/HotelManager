package com.hotely5d.entity;
import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;

/**
* Notice实体类
*/
@TableName("notice")
public class Notice {
    @TableId("id")
    private Integer id;// ID
	@TableField("content")
    private String content;// 公告内容
	@TableField("create_time")
    private Date createTime;// 发布时间
    @TableField("title")
    private String title;// 标题

    public Notice() {
        super();
    }

    public  Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public  String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public  Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}