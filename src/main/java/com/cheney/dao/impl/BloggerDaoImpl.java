package com.cheney.dao.impl;

import com.cheney.dao.BloggerDao;
import com.cheney.entity.jpa.Blogger;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

/**
 * 博主 - dao
 */
@Repository("bloggerDaoImpl")
public class BloggerDaoImpl extends BaseDaoImpl<Blogger, Long> implements BloggerDao {

    @Override
    public Blogger findByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        String jpql = "select blogger from Blogger blogger where blogger.username = :username";
        try {
            return entityManager.createQuery(jpql, Blogger.class).setParameter("username", username).setFlushMode(FlushModeType.COMMIT).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
