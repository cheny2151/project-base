package com.cheney.entity.mongo;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

public class MongoBaseEntity implements Serializable {

    private static final long serialVersionUID = 1255071305635970297L;

    @Id
    private String id;

    /**
     * 创建时间
     */
    private Date createDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
