package com.cheney.entity;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * 博主
 */
@Entity
@Table(name = "m_blogger", indexes = {@Index(columnList = "username")})
public class Blogger extends BaseEntity {

    private static final long serialVersionUID = 5004361985367958141L;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 账号
     */
    private String username;

    /**
     * 座右铭
     */
    private String motto;

    /**
     * 登陆状态
     */
    private Boolean loginStatus;

    /**
     * 等级
     */
    private Integer level;

    public Blogger() {
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public Boolean getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickName='" + nickName + '\'' +
                ", username='" + username + '\'' +
                ", motto='" + motto + '\'' +
                ", loginStatus=" + loginStatus +
                ", level=" + level +
                '}';
    }
}
