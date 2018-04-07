package com.cheney.entity.jpa;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class UserBase extends BaseEntity {

    private static final long serialVersionUID = 3896539459527873185L;

    private String username;

    @Column(unique = true, updatable = false, nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
