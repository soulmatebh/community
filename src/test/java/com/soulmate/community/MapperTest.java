package com.soulmate.community;

import com.soulmate.community.dao.DiscussPostMapper;
import com.soulmate.community.dao.LoginTicketMapper;
import com.soulmate.community.dao.MessageMapper;
import com.soulmate.community.dao.UserMapper;
import com.soulmate.community.entity.DiscussPost;
import com.soulmate.community.entity.LoginTicket;
import com.soulmate.community.entity.Message;
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

    @Autowired
    MessageMapper messageMapper;

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

    @Test
    public void testMessage() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        messages.forEach(a -> System.out.println(a));

        int i = messageMapper.selectConversationCount(111);
        System.out.println(i);

        List<Message> messages1 = messageMapper.selectLetters("111_112", 0, 10);
        messages1.forEach(a -> System.out.println(a));

        int i1 = messageMapper.selectLetterCount("111_112");
        System.out.println(i1);

        int i2 = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(i2);
    }
}
