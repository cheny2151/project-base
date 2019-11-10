package com.cheney.dao.mybatis;

import com.cheney.entity.Role;
import org.springframework.stereotype.Repository;

@Repository("roleMapper")
public interface RoleMapper extends BaseMapper<Role, Long> {

}
