package com.jiahao.controller;

import com.jiahao.bean.Emp;
import com.jiahao.dao.EmpMapper;
import com.jiahao.service.EmpService;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.PrivateKeyResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("emps")
public class EmpController {

    @Resource
    private EmpService empService;

    @GetMapping
    public void findAll(){
        List<Emp> emps = empService.findAll();
        for(Emp emp : emps){
            System.out.println(emp);
        }
    }
}
