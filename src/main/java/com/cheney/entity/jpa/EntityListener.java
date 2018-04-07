package com.cheney.entity.jpa;

import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import java.util.Date;

/**
 * 监听entity
 */
@Component
public class EntityListener {

    @PrePersist
    public void setCreateDate(BaseEntity baseEntity) {
        baseEntity.setCreateDate(new Date());
    }

}
