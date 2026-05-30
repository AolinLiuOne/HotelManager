package com.hotely5d.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hotely5d.dao.MemberMapper;
import com.hotely5d.entity.Admin;
import com.hotely5d.entity.Member;
import com.hotely5d.entity.model.Result;
import com.hotely5d.entity.model.StatusCode;
import com.hotely5d.entity.query.LoginQuery;
import com.hotely5d.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MemberService {

    @Autowired
    private MemberMapper memberMapper;

    public List<Member> findAll() {
        return memberMapper.selectList(null);
    }

    public Page<Member> search(Page<Member> page,Member member) {
        return page.setRecords( memberMapper.selectPage(page,getEntityWrapper(member)));
    }

    public List<Member> search(Member member) {
        return memberMapper.selectList(getEntityWrapper(member));
    }

    //条件构造器
    private EntityWrapper getEntityWrapper(Member member){
        EntityWrapper entityWrapper = new EntityWrapper();
        if(null != member){
            if(!StringUtils.isEmpty(member.getUsername())){
                entityWrapper.like("username",String.valueOf(member.getUsername()));
            }
            if(!StringUtils.isEmpty(member.getPassword())){
                entityWrapper.like("password",String.valueOf(member.getPassword()));
            }
            if(!StringUtils.isEmpty(member.getName())){
                entityWrapper.like("name",String.valueOf(member.getName()));
            }
            if(!StringUtils.isEmpty(member.getPhone())){
                entityWrapper.like("phone",String.valueOf(member.getPhone()));
            }
            if(!StringUtils.isEmpty(member.getIdcard())){
                entityWrapper.like("idcard",String.valueOf(member.getIdcard()));
            }
            if(!StringUtils.isEmpty(member.getHead())){
                entityWrapper.like("head",String.valueOf(member.getHead()));
            }
        }
        return entityWrapper;
    }

    public void modify(Member member) {
        memberMapper.updateById(member);
    }

    public void add(Member member) {
        if(member.getHead()==null){
            member.setHead("b11.jpg");
        }
        member.setCreateTime(new Date());
        memberMapper.insert(member);
    }

    public Member findById(Integer id) {
        return memberMapper.selectById(id);
    }

    public void removeById(Integer id) {
        memberMapper.deleteById(id);
    }

    public Result login(LoginQuery query) {
        List<Member> list = memberMapper.selectList(new EntityWrapper().eq("username", query.getUsername()).eq("password", query.getPassword()));
        if(list.size() == 0) return new Result(false, StatusCode.ERROR,"账号或密码错误");
        //返回Token
        Map<String,String> payloadMap = new HashMap();
        payloadMap.put("id",list.get(0).getId().toString());
        payloadMap.put("role","2");
        String token = JWTUtils.getToken(payloadMap);
        return new Result(true, StatusCode.OK,"登录成功",token);
    }

    public Result register(Member member) {
        List<Member> list = memberMapper.selectList(new EntityWrapper().eq("username", member.getUsername()));
        if(list.size() != 0)return new Result(false, StatusCode.ERROR,"账号已被注册！");

        list = memberMapper.selectList(new EntityWrapper().eq("idcard", member.getIdcard()));
        if(list.size() != 0)return new Result(false, StatusCode.ERROR,"身份证号已被注册！");
        if(member.getHead() == null){
            member.setHead("b11.jpg");
        }
        member.setCreateTime(new Date());
        memberMapper.insert(member);
        return new Result(true, StatusCode.OK,"注册成功");
    }
}
