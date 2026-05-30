package com.hotely5d.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.entity.Notice;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
    * 查询最新五条公告
    * @return
    */
    @GetMapping("getTop5")
    public Result getTop5(){
        List<Notice> noticeList = noticeService.getTop5();
        return new Result(true, StatusCode.OK,"查询成功",noticeList);
    }

    /**
    * 根据条件分页查询公告
    * @param notice
    * @return
    */
    @GetMapping("search/{current}/{size}")
    public Result search(@PathVariable Integer current,@PathVariable Integer size ,Notice notice){
        Page<Notice> page = noticeService.search(new Page<Notice>(current, size), notice);
        return new Result(true, StatusCode.OK,"查询成功",page);
    }

    /**
    * 新增公告
    * @param notice
    * @return
    */
    @PostMapping
    public Result add(@RequestBody Notice notice){
        notice.setCreateTime(new Date());
        noticeService.add(notice);
        return new Result(true, StatusCode.OK,"新增成功");
    }

    /**
    * 修改公告
    * @param notice
    * @return
    */
    @PutMapping
    public Result modify(@RequestBody Notice notice){
        noticeService.modify(notice);
        return new Result(true, StatusCode.OK,"修改成功");
    }

    /**
    * 根据id查询公告
    * @param id
    * @return
    */
    @GetMapping("{id}")
    public Result findById(@PathVariable("id") Integer id){
        Notice notice  = noticeService.findById(id);
        return new Result(true, StatusCode.OK,"查询成功",notice);
    }

    /**
    * 根据id删除公告
    * @param id
    * @return
    */
    @DeleteMapping("{id}")
    public Result removeById(@PathVariable("id") Integer id){
        noticeService.removeById(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }

}