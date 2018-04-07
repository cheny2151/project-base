package com.cheney.service.impl;

import com.cheney.dao.BaseDao;
import com.cheney.dao.BloggerDao;
import com.cheney.entity.jpa.Blogger;
import com.cheney.service.BloggerService;
import com.cheney.utils.security.AuthUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional
@Service("bloggerServiceImpl")
public class BloggerServiceImpl extends BaseServiceImpl<Blogger, Long> implements BloggerService {

    @Resource(name = "bloggerDaoImpl")
    private BloggerDao bloggerDao;

    @Override
    @Resource(name = "bloggerDaoImpl")
    protected void setBaseDao(BaseDao<Blogger, Long> baseDao) {
        super.setBaseDao(baseDao);
    }

    @Override
    public Blogger getCurrent() {
        return bloggerDao.findByUsername(AuthUtils.getCurrentUsername());
    }
}
