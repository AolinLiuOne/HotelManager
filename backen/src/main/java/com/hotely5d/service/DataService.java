package com.hotely5d.service;

import com.hotely5d.dao.AppointmentMapper;
import com.hotely5d.dao.OrdersMapper;
import com.hotely5d.entity.MonthlyIncome;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class DataService {

    @Resource
    private AppointmentMapper appointmentMapper;

    @Resource
    private OrdersMapper ordersMapper;


    public List<MonthlyIncome> getTotalMonthlyIncome(Integer hotelId) {
        // 查询两张表

        List<MonthlyIncome> ordersIncome = ordersMapper.getMonthlyIncomeByHotelId(hotelId);


        Map<String, BigDecimal> totalMap = new HashMap<>();



        // 累加 orders
        for (MonthlyIncome dto : ordersIncome) {
            totalMap.put(dto.getMonth(), totalMap.getOrDefault(dto.getMonth(), BigDecimal.ZERO).add(dto.getIncome()));
        }

        // 转回 List
        List<MonthlyIncome> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : totalMap.entrySet()) {
            result.add(new MonthlyIncome(entry.getKey(), entry.getValue()));
        }

        // 按月份排序（升序）
        result.sort(Comparator.comparing(MonthlyIncome::getMonth));

        return result;
    }

}

