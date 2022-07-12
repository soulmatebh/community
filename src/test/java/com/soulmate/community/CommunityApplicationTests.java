package com.soulmate.community;

import com.soulmate.community.dao.MyDAO;
import com.soulmate.community.service.MyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootTest
class CommunityApplicationTests{

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    MyDAO myDAO;

    @Test
    public void testApplicationContext() {
        System.out.println(applicationContext);
//        MyDAO bean = applicationContext.getBean(MyDAO.class);
        MyDAO bean = applicationContext.getBean("mybean", MyDAO.class);
        System.out.println(bean.hello());

    }

    @Test
    public void testService() {
        MyService bean = applicationContext.getBean(MyService.class);   //默认单例模式
        System.out.println(bean);
        bean = applicationContext.getBean((MyService.class));
        System.out.println(bean);
    }

    @Test
    public void testDAO() {
        System.out.println(myDAO.hello());
    }
}
