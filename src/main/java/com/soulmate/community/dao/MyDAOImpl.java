package com.soulmate.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * Administrator
 * 2022/7/11  19:11
 * community
 */
@Repository("mybean")
@Primary
public class MyDAOImpl implements MyDAO {
    @Override
    public String hello() {
        return "HelloTest";
    }
}
