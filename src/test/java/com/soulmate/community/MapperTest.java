package com.soulmate.community;

import com.soulmate.community.dao.DiscussPostMapper;
import com.soulmate.community.dao.LoginTicketMapper;
import com.soulmate.community.dao.UserMapper;
import com.soulmate.community.entity.DiscussPost;
import com.soulmate.community.entity.LoginTicket;
import com.soulmate.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

/**
 * Administrator
 * 2022/7/11  20:54
 * community
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    UserMapper userMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Test
    public void testUser() {
        User user = userMapper.selectById(102);
        System.out.println(user);
    }

    @Test
    public void testDiscussPost() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 1, 10, 1);
        discussPosts.forEach(a -> System.out.println(a));

        int i = discussPostMapper.selectDiscussPostRows(101);
        System.out.println(i);
    }

    @Test
    public void testLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket("asd");
        loginTicket.setUserId(101);
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        int i = loginTicketMapper.insertLoginTicket(loginTicket);

        LoginTicket ticket1 = loginTicketMapper.selectByTicket("asd");
        System.out.println(ticket1);

        int asd = loginTicketMapper.updateStatus("asd", 1);

        LoginTicket ticket2 = loginTicketMapper.selectByTicket("asd");
        System.out.println(ticket2);

    }
}
