package com.soulmate.community.controller;

import com.soulmate.community.entity.Comment;
import com.soulmate.community.entity.DiscussPost;
import com.soulmate.community.entity.User;
import com.soulmate.community.service.CommentService;
import com.soulmate.community.service.DiscussPostService;
import com.soulmate.community.util.CommunityConstant;
import com.soulmate.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.Date;

/**
 * @author zhengguohuang
 * @date 2021/03/19
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

//    @Autowired
//    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

//    @Autowired
//    private RedisTemplate redisTemplate;

    /**
     * 添加评论
     *
     * @param discussPostId
     * @param comment
     * @return
     */
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

//        // 触发事件
//        Event event =
//                new Event()
//                        .setTopic(TOPIC_COMMENT)
//                        .setUserId(comment.getUserId())
//                        .setEntityType(comment.getEntityType())
//                        .setEntityId(comment.getEntityId())
//                        .setData("postId", discussPostId);
//        if (comment.getEntityType() == ENTITY_TYPE_POST) {
//            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
//            event.setEntityUserId(target.getUserId());
//        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
//            Comment target = commentService.findCommentsById(comment.getEntityId());
//            event.setEntityUserId(target.getUserId());
//        }
//        eventProducer.fireEvent(event);
//
//        if (comment.getEntityType() == ENTITY_TYPE_POST) {
//            // 触发发帖事件
//            event =
//                    new Event()
//                            .setTopic(TOPIC_PUBLISH)
//                            .setUserId(comment.getUserId())
//                            .setEntityType(ENTITY_TYPE_POST)
//                            .setEntityId(discussPostId);
//            eventProducer.fireEvent(event);
//            // 计算帖子的分数
//            String redisKey = RedisKeyUtil.getPostScoreKey();
//            redisTemplate.opsForSet().add(redisKey, discussPostId);
//        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
