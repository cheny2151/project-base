package com.cheney.dao.impl;

import com.cheney.dao.UserDao;
import com.cheney.entity.AuthUser;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.stereotype.Repository;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

/**
 * 用户认证安全类 - dao
 */
@Repository("userDaoImpl")
public class UserDaoImpl extends BaseDaoImpl<AuthUser, Long> implements UserDao {

    @Override
    public AuthUser findByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        String jpql = "select user from AuthUser user where user.username = :username";
        try {
            return entityManager.createQuery(jpql, AuthUser.class).setParameter("username", username).setFlushMode(FlushModeType.COMMIT).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
