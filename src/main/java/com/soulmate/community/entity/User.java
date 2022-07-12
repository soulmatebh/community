package com.soulmate.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * Administrator
 * 2022/7/11  20:46
 * community
 */
@Data
public class User {
    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private int type;
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;
}
