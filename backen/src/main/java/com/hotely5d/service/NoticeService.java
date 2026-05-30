package com.hotely5d.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.dao.NoticeMapper;
import com.hotely5d.entity.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
@Transactional
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    public List<Notice> findAll() {
        return noticeMapper.selectList(null);
    }

    public Page<Notice> search(Page<Notice> page,Notice notice) {
        return page.setRecords( noticeMapper.selectPage(page,getEntityWrapper(notice)));
    }

    public List<Notice> search(Notice notice) {
        return noticeMapper.selectList(getEntityWrapper(notice));
    }

    //条件构造器
    private EntityWrapper getEntityWrapper(Notice notice){
        EntityWrapper entityWrapper = new EntityWrapper();
        if(null != notice){
            if(!StringUtils.isEmpty(notice.getContent())){
                entityWrapper.like("content",String.valueOf(notice.getContent()));
            }
            if(!StringUtils.isEmpty(notice.getTitle())){
                entityWrapper.like("title",String.valueOf(notice.getTitle()));
            }
        }
        entityWrapper.orderBy("create_time",false);
        return entityWrapper;
    }

    public void modify(Notice notice) {
        noticeMapper.updateById(notice);
    }

    public void add(Notice notice) {
        noticeMapper.insert(notice);
    }

    public Notice findById(Integer id) {
        return noticeMapper.selectById(id);
    }

    public void removeById(Integer id) {
        noticeMapper.deleteById(id);
    }

    public List<Notice> getTop5() {
        return noticeMapper.selectList(new EntityWrapper().orderBy("createTime",false).last("limit 5"));
    }
}
