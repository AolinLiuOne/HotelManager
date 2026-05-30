package com.hotely5d.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hotely5d.entity.MonthlyIncome;
import org.springframework.stereotype.Repository;
import com.hotely5d.entity.Orders;

import java.util.List;
import java.util.Map;


@Repository
public interface OrdersMapper extends BaseMapper<Orders>{

    List<MonthlyIncome> getMonthlyIncomeByHotelId(Integer hotelId);

}