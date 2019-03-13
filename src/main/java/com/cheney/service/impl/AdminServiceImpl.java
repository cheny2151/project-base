package com.cheney.service.impl;

import com.cheney.dao.mybatis.BaseMapper;
import com.cheney.dao.mybatis.AdminMapper;
import com.cheney.entity.dto.Admin;
import com.cheney.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Admin - serviceImpl
 */
@Service("adminServiceImpl")
@Transactional
public class AdminServiceImpl extends BaseServiceImpl<Admin, Long> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    @Override
    protected void setBaseMapper(BaseMapper<Admin, Long> baseMapper) {
        super.setBaseMapper(baseMapper);
    }

}
