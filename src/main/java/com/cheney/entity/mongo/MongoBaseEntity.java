package com.cheney.entity.mongo;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class MongoBaseEntity implements Serializable {

    private static final long serialVersionUID = 1255071305635970297L;

    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
