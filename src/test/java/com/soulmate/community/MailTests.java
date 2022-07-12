package com.soulmate.community;

import com.soulmate.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;      //主动调用模板引擎
    @Test
    public void testTextMail(){
        mailClient.sendMail("1349890811@qq.com", "Test", "welcome");
    }
    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username", "帅气逼人的VIP");
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("1349890811@qq.com", "HTML", content);

    }
}
