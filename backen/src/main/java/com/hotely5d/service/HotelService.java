package com.hotely5d.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hotely5d.dao.HotelMapper;
import com.hotely5d.entity.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HotelService {
    @Autowired
    HotelMapper hotelMapper;

    public void add(Hotel hotel) {
        Map<String,Object> map = new HashMap<>();
        map.put("hotel",hotel.getHotel());
        List<Hotel> hotels = hotelMapper.selectByMap(map);
        if(hotels.isEmpty()){
            hotelMapper.insert(hotel);
        }
    }

    public void update(Hotel hotel) {
        hotelMapper.updateById(hotel);
    }

    public void delete(int id) {
        hotelMapper.deleteById(id);
    }

    public Hotel findById(int id) {
        return hotelMapper.selectById(id);
    }

    public List<Hotel> findAll() {
        return hotelMapper.selectList(null);
    }

    public List<Hotel> selectByHotelName(String hotelName) {
        Map<String,Object> map = new HashMap<>();
        map.put("hotel",hotelName);
        return hotelMapper.selectByMap(map);
    }

    public List<Hotel> selectByHotelId(Integer hotelId) {
        Map<String,Object> map = new HashMap<>();
        map.put("hotel",hotelId);
        return hotelMapper.selectByMap(map);
    }

    public EntityWrapper getHotelWrapper(Integer hotelId) {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("hotel_id",hotelId);
        return entityWrapper;
    }

    public Hotel getById(Integer hotelId) {
        return hotelMapper.selectById(hotelId);
    }
}
