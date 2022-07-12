package com.soulmate.community.service;

import com.soulmate.community.dao.UserMapper;
import com.soulmate.community.entity.User;
//import org.apache.commons.lang3.StringUtils;
import com.soulmate.community.util.CommunityConstant;
import com.soulmate.community.util.CommunityUtil;
import com.soulmate.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Administrator
 * 2022/7/11  21:33
 * community
 */
@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;



    public User findUserById(int id) {
        return userMapper.selectById(id);
//        User user = getCache(id);
//        if (user == null) {
//            user = initCache(id);
//        }
//        return user;
    }

    /** 注册 */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>(16);
        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号为空啦老弟，不行哦");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码为空啦老弟，想个密码吧");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱为空啦老弟，让我怎么联系你呢？");
            return map;
        }
//
        // 验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }
        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册！");
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(
                String.format(
                        "http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url =
                domain
                        + contextPath
                        + "/activation/"
                        + user.getId()
                        + "/"
                        + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活邮件", content);

        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
//            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILED;
        }
    }
//
//    /**
//     * 登录
//     *
//     * @param username 用户名
//     * @param password 密码
//     * @param expiredSeconds 凭证过期时间 秒
//     * @return
//     */
//    public Map<String, Object> login(String username, String password, int expiredSeconds) {
//        Map<String, Object> map = new HashMap<>(16);
//
//        // 空值处理
//        if (StringUtils.isBlank(username)) {
//            map.put("usernameMsg", "账号不能为空！");
//            return map;
//        }
//        if (StringUtils.isBlank(password)) {
//            map.put("passwordMsg", "密码不能为空！");
//            return map;
//        }
//        // 验证账号
//        User user = userMapper.selectByName(username);
//        if (user == null) {
//            map.put("usernameMsg", "该账号不存在！");
//            return map;
//        }
//
//        // 验证状态
//        if (user.getStatus() == 0) {
//            map.put("usernameMsg", "该账号未激活！");
//            return map;
//        }
//        // 验证密码
//        password = CommunityUtil.md5(password + user.getSalt());
//        if (!user.getPassword().equals(password)) {
//            map.put("passwordMsg", "密码不正确！");
//            return map;
//        }
//
//        // 生成登录凭证
//
//        LoginTicket loginTicket = new LoginTicket();
//        loginTicket.setUserId(user.getId());
//        loginTicket.setTicket(CommunityUtil.generateUUID());
//        loginTicket.setStatus(0);
//        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        //        loginTicketMapper.insertLoginTicket(loginTicket);
//
//        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
//        // loginTicket会序列化成json
//        redisTemplate.opsForValue().set(redisKey, loginTicket);
//
//        map.put("ticket", loginTicket.getTicket());
//        return map;
//    }
//
//    public void logout(String ticket) {
//        //        loginTicketMapper.updateStatus(ticket, 1);
//        String redisKey = RedisKeyUtil.getTicketKey(ticket);
//        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
//        loginTicket.setStatus(1);
//        redisTemplate.opsForValue().set(redisKey, loginTicket);
//    }
//
//    /**
//     * 通过ticket查询LoginTicket对象
//     *
//     * @param ticket
//     * @return
//     */
//    public LoginTicket findLoginTicket(String ticket) {
//        //        return loginTicketMapper.selectByTicket(ticket);
//        String redisKey = RedisKeyUtil.getTicketKey(ticket);
//        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
//    }
//
//    /**
//     * 更新头像
//     *
//     * @param userId
//     * @param headerUrl
//     * @return
//     */
//    public int updateHeader(int userId, String headerUrl) {
//        //        return userMapper.updateHeader(userId, headerUrl);
//        int rows = userMapper.updateHeader(userId, headerUrl);
//        clearCache(userId);
//        return rows;
//    }
//
//    /**
//     * 通过用户名查找用户
//     *
//     * @param username
//     * @return
//     */
//    public User findUserByName(String username) {
//        return userMapper.selectByName(username);
//    }
//
//    /**
//     * 1.优先从缓存中取值
//     *
//     * @param userId
//     * @return
//     */
//    private User getCache(int userId) {
//        String redisKey = RedisKeyUtil.getUserKey(userId);
//        return (User) redisTemplate.opsForValue().get(redisKey);
//    }
//
//    /**
//     * 2.取不到时初始化缓存数据
//     *
//     * @param userId
//     * @return
//     */
//    private User initCache(int userId) {
//        User user = userMapper.selectById(userId);
//        String redisKey = RedisKeyUtil.getUserKey(userId);
//        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
//        return user;
//    }
//
//    private void clearCache(int userId) {
//        String redisKey = RedisKeyUtil.getUserKey(userId);
//        redisTemplate.delete(redisKey);
//    }
//
//    /**
//     * 获得用户权限
//     *
//     * @param userId
//     * @return
//     */
//    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
//        User user = this.findUserById(userId);
//
//        List<GrantedAuthority> list = new ArrayList<>();
//        list.add(
//                () -> {
//                    switch (user.getType()) {
//                        case 1:
//                            return AUTHORITY_ADMIN;
//                        case 2:
//                            return AUTHORITY_MODERATOR;
//                        default:
//                            return AUTHORITY_USER;
//                    }
//                });
//        return list;
//    }
//
//    /**
//     * 更新密码
//     *
//     * @param id
//     * @param newPassword
//     * @return
//     */
//    public int updatePassword(int id, String newPassword) {
//        clearCache(id);
//        return userMapper.updatePassword(id, newPassword);
//    }
}
