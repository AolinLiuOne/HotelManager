package com.hotely5d.entity;
import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableId;

/**
* Message实体类
*/
@TableName("message")
public class Message {
    @TableId("id")
    private Integer id;// ID
	@TableField("member_id")
    private Integer memberId;// 用户
	@TableField("comment")
    private String comment;// 留言
	@TableField("reply")
    private String reply;// 回复
	@TableField("create_time")
    private Date createTime;// 留言时间
	@TableField("reply_time")
    private Date replyTime;// 回复时间
	@TableField("status")
    private Integer status;// 状态

    @TableField(exist = false)
    private Member member = new Member();

    public Message() {
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
    public  String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public  String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
    public  Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public  Date getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }
    public  Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}