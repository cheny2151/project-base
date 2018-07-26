package com.cheney.dao.mybatis;

import com.cheney.entity.dto.Admin;
import org.springframework.stereotype.Repository;

@Repository("adminMapper")
public interface AdminMapper extends BaseMapper<Admin, Long> {

}
