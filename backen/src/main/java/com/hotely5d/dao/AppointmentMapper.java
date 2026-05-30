package com.hotely5d.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hotely5d.entity.MonthlyIncome;
import org.springframework.stereotype.Repository;
import com.hotely5d.entity.Appointment;

import java.util.List;
import java.util.Map;


@Repository
public interface AppointmentMapper extends BaseMapper<Appointment>{

    List<MonthlyIncome> getMonthlyIncomeByHotelId(Integer hotelId);


}