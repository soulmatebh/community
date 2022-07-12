package com.soulmate.community;

import com.soulmate.community.dao.DiscussPostMapper;
import com.soulmate.community.dao.UserMapper;
import com.soulmate.community.entity.DiscussPost;
import com.soulmate.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

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
}
