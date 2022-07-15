package com.soulmate.community.service;

import com.soulmate.community.dao.DiscussPostMapper;
import com.soulmate.community.dao.UserMapper;
import com.soulmate.community.entity.DiscussPost;
import com.soulmate.community.entity.User;
import com.soulmate.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * Administrator
 * 2022/7/11  19:17
 * community
 */
@Service
//@Scope("prototype")     //默认单例，设置为原型模式(多例模式)
public class MyService {


    @Autowired
    UserMapper userMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;

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

    /**
     * 事务传播机制：
     * REQUIRED:支持当前事务(外部事务),如果不存在则创建新事务
     * REQUIRES_NEW:创建一个新事务，并且暂停当前事务(外部事务)
     * NESTED:如果当前存在事务(外部事务)则嵌套在该事务中执行(独立的提交和回滚)，否则就和REQUIRED一样
     *
     * @return
     */
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class)
    public Object save1() {
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("hello");
        post.setContent("新人报道！");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");     //出现异常，程序回滚
        return "ok";
    }

    @Autowired
    private TransactionTemplate transactionTemplate;

    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(
                (TransactionCallback<Object>)
                        status -> {
                            // 新增用户
                            User user = new User();
                            user.setUsername("alpha2");
                            user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                            user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                            user.setEmail("alpha2@qq.com");
                            user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                            user.setCreateTime(new Date());
                            userMapper.insertUser(user);

                            // 新增帖子
                            DiscussPost post = new DiscussPost();
                            post.setUserId(user.getId());
                            post.setTitle("hello2");
                            post.setContent("新人报道2！");
                            post.setCreateTime(new Date());
                            discussPostMapper.insertDiscussPost(post);

                            Integer.valueOf("abc");
                            return "ok";
                        });
    }

}
