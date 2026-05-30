package com.hotely5d.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.entity.Member;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.entity.query.LoginQuery;
import com.hotely5d.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 登录
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody LoginQuery query){
        return memberService.login(query);
    }

    /**
    * 查询全部
    * @return
    */
    @GetMapping
    public Result findAll(){
    List<Member> memberList = memberService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",memberList);
    }

    /**
    * 根据条件查询
    * @param member
    * @return
    */
    @GetMapping("search")
    public Result search(Member member){
        List<Member> memberList = memberService.search(member);
        return new Result(true, StatusCode.OK,"查询成功",memberList);
    }

    /**
    * 根据条件分页查询
    * @param member
    * @return
    */
    @GetMapping("search/{current}/{size}")
    public Result search(@PathVariable Integer current,@PathVariable Integer size ,Member member){
        Page<Member> page = memberService.search(new Page<Member>(current, size), member);
        return new Result(true, StatusCode.OK,"查询成功",page);
    }

    /**
    * 新增
    * @param member
    * @return
    */
    @PostMapping
    public Result add(@RequestBody Member member){
        memberService.add(member);
        return new Result(true, StatusCode.OK,"新增成功");
    }

    /**
    * 修改
    * @param member
    * @return
    */
    @PutMapping
    public Result modify(@RequestBody Member member){
        memberService.modify(member);
        return new Result(true, StatusCode.OK,"修改成功");
    }

    /**
    * 根据id查询
    * @param id
    * @return
    */
    @GetMapping("{id}")
    public Result findById(@PathVariable("id") Integer id){
        Member member  = memberService.findById(id);
        return new Result(true, StatusCode.OK,"查询成功",member);
    }

    /**
    * 根据id删除
    * @param id
    * @return
    */
    @DeleteMapping("{id}")
    public Result removeById(@PathVariable("id") Integer id){
        memberService.removeById(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }

}