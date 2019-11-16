package com.jiahao.service.impl;

import com.jiahao.bean.Emp;
import com.jiahao.dao.EmpMapper;
import com.jiahao.service.EmpService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EmpServiceImpl implements EmpService {

    @Resource
    private EmpMapper empMapper;

    @Override
    public List<Emp> findAll() {
        return empMapper.selectByExample(null);
    }
}
