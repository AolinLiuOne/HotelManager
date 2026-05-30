package com.hotely5d.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.entity.Message;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.service.MessageService;
import com.hotely5d.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
    * 查询全部
    * @return
    */
    @GetMapping
    public Result findAll(){
    List<Message> messageList = messageService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",messageList);
    }

    /**
    * 根据条件查询
    * @param message
    * @return
    */
    @GetMapping("search")
    public Result search(Message message){
        List<Message> messageList = messageService.search(message);
        return new Result(true, StatusCode.OK,"查询成功",messageList);
    }

    /**
     * 根据条件分页查询
     * @param message
     * @return
     */
    @GetMapping("search/{current}/{size}")
    public Result search(@PathVariable Integer current,@PathVariable Integer size ,Message message){
        Page<Message> page = messageService.search(new Page<Message>(current, size), message);
        return new Result(true, StatusCode.OK,"查询成功",page);
    }

    /**
    * 用户留言
    * @param message
    * @return
    */
    @PostMapping
    public Result add(@RequestBody Message message, HttpServletRequest request){
        Long userId = JWTUtils.getUserId(request);
        message.setMemberId(userId.intValue());
        message.setCreateTime(new Date());
        messageService.add(message);
        return new Result(true, StatusCode.OK,"操作成功");
    }

    /**
    * 回复留言
    * @param message
    * @return
    */
    @PutMapping
    public Result modify(@RequestBody Message message){
        message.setStatus(2);
        message.setReplyTime(new Date());
        messageService.modify(message);
        return new Result(true, StatusCode.OK,"操作成功");
    }

    /**
    * 根据id查询
    * @param id
    * @return
    */
    @GetMapping("{id}")
    public Result findById(@PathVariable("id") Integer id){
        Message message  = messageService.findById(id);
        return new Result(true, StatusCode.OK,"查询成功",message);
    }

    /**
    * 根据id删除
    * @param id
    * @return
    */
    @DeleteMapping("{id}")
    public Result removeById(@PathVariable("id") Integer id){
        messageService.removeById(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }

}