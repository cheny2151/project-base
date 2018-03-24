package com.cheney.dao.impl;

import com.cheney.dao.AdminDao;
import com.cheney.entity.Admin;
import org.springframework.stereotype.Repository;

/**
 * admin - dao
 */
@Repository("adminDaoImpl")
public class AdminDaoImpl extends BaseDaoImpl<Admin, Long> implements AdminDao {

}
