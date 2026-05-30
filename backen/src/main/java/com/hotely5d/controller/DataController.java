package com.hotely5d.controller;

import com.hotely5d.entity.MonthlyIncome;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.service.DataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/data")
public class DataController {

    @Resource
    private DataService dataService;

    @GetMapping("/monthlyIncome")
    public Result getMonthlyIncome(@RequestParam Integer hotelId) {
        List<MonthlyIncome> res = dataService.getTotalMonthlyIncome(hotelId);
        return new Result(true, StatusCode.OK,"查询成功",res);
    }
}
