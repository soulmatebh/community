package com.soulmate.community.util;

import com.soulmate.community.entity.User;
import org.springframework.stereotype.Component;


/**
 * 持有用户信息，用于代替session对象
 * 并在多线程并发环境下，将用户存入ThreadLocal中，对各个线程进行隔离
 *
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
