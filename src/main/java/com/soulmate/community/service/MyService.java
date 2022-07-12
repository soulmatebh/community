package com.soulmate.community.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Administrator
 * 2022/7/11  19:17
 * community
 */
@Service
//@Scope("prototype")     //默认单例，设置为原型模式(多例模式)
public class MyService {
    public MyService() {
        System.out.println("实例化服务");
    }

    @PostConstruct
    public void init() {
        System.out.println("初始化服务");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("销毁服务");
    }

    public String service() {
        return "提供特殊服务";
    }

}
