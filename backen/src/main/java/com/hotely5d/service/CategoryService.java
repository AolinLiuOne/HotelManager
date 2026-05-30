package com.hotely5d.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.dao.CategoryMapper;
import com.hotely5d.dao.RoomMapper;
import com.hotely5d.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private RoomMapper roomMapper;

    public List<Category> findAll(Integer hotelId) {
        List<Category> categories = categoryMapper.selectList(new EntityWrapper<Category>().eq("hotel_id",hotelId));
        for (Category category : categories) {
            Integer count = roomMapper.selectCount(new EntityWrapper().eq("category_id", category.getId()).eq("status", 1));
            category.setRoomNum(count);
        }
        return categories;
    }

    public List<Category> searchAll() {
        return categoryMapper.selectList(null);
    }

    public Page<Category> search(Page<Category> page, Category category, Integer hotelId) {
        // 在原有的条件基础上加 hotelId 限制
        EntityWrapper wrapper = getEntityWrapper(category);
        if (hotelId != null) {
            wrapper.eq("hotel_id", hotelId);
        }

        List<Category> list = categoryMapper.selectPage(page, wrapper);

        for (Category c : list) {
            EntityWrapper countWrapper = new EntityWrapper();
            countWrapper.eq("category_id", c.getId()).eq("status", 1);
            if (hotelId != null) {
                countWrapper.eq("hotel_id", hotelId);
            }
            Integer count = roomMapper.selectCount(countWrapper);
            c.setRoomNum(count);
        }

        return page.setRecords(list);
    }


    public List<Category> search(Category category, Integer hotelId) {
        // 在原有条件上加 hotel_id 限制
        EntityWrapper<Category> wrapper = getEntityWrapper(category);
        if (hotelId != null) {
            wrapper.eq("hotel_id", hotelId);
        }

        List<Category> list = categoryMapper.selectList(wrapper);
        for (Category c : list) {
            EntityWrapper countWrapper = new EntityWrapper();
            countWrapper.eq("category_id", c.getId()).eq("status", 1);
            if (hotelId != null) {
                countWrapper.eq("hotel_id", hotelId);
            }
            Integer count = roomMapper.selectCount(countWrapper);
            c.setRoomNum(count == null ? 0 : count);
        }
        return list;
    }


    //条件构造器
    private EntityWrapper getEntityWrapper(Category category){
        EntityWrapper entityWrapper = new EntityWrapper();
        if(null != category){
            if(!StringUtils.isEmpty(category.getCategoryName())){
                entityWrapper.like("category_name",String.valueOf(category.getCategoryName()));
            }
            if(!StringUtils.isEmpty(category.getPhoto())){
                entityWrapper.like("photo",String.valueOf(category.getPhoto()));
            }
            if(!StringUtils.isEmpty(category.getArea())){
                entityWrapper.like("area",String.valueOf(category.getArea()));
            }
            if(!StringUtils.isEmpty(category.getIntroduce())){
                entityWrapper.like("introduce",String.valueOf(category.getIntroduce()));
            }
        }
        return entityWrapper;
    }

    public void modify(Category category) {
        categoryMapper.updateById(category);
    }

    public void add(Category category) {
        categoryMapper.insert(category);
    }

    public Category findById(Integer id) {
        Category c = categoryMapper.selectById(id);
        if(c == null){
            c=new Category();
        }
        Integer count = roomMapper.selectCount(new EntityWrapper().eq("category_id", c.getId()).eq("status", 1));
        c.setRoomNum(count);
        return c;
    }

    public void removeById(Integer id, Integer hotelId) {
        EntityWrapper<Category> wrapper = new EntityWrapper<>();
        wrapper.eq("id", id)
                .eq(hotelId != null, "hotel_id", hotelId);

        categoryMapper.delete(wrapper);
    }

}
