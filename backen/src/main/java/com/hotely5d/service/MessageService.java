package com.hotely5d.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.dao.MemberMapper;
import com.hotely5d.dao.MessageMapper;
import com.hotely5d.entity.Member;
import com.hotely5d.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MemberMapper memberMapper;

    public List<Message> findAll() {
        return messageMapper.selectList(null);
    }

    public Page<Message> search(Page<Message> page,Message message) {
        List<Message> list = messageMapper.selectPage(page, getEntityWrapper(message));
        for (Message m : list) {
            Member member = memberMapper.selectById(m.getMemberId());
            m.setMember(member);
        }
        return page.setRecords(list);
    }

    public List<Message> search(Message message) {
        return messageMapper.selectList(getEntityWrapper(message));
    }

    //条件构造器
    private EntityWrapper getEntityWrapper(Message message){
        EntityWrapper entityWrapper = new EntityWrapper();
        if(null != message){
            if(!StringUtils.isEmpty(message.getComment())){
                entityWrapper.like("comment",String.valueOf(message.getComment()));
            }
            if(!StringUtils.isEmpty(message.getReply())){
                entityWrapper.like("reply",String.valueOf(message.getReply()));
            }
        }
        entityWrapper.orderBy("create_time",false);
        return entityWrapper;
    }

    public void modify(Message message) {
        messageMapper.updateById(message);
    }

    public void add(Message message) {
        messageMapper.insert(message);
    }

    public Message findById(Integer id) {
        return messageMapper.selectById(id);
    }

    public void removeById(Integer id) {
        messageMapper.deleteById(id);
    }
}
