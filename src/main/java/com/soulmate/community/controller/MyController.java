package com.soulmate.community.controller;

import com.soulmate.community.service.MyService;
import com.soulmate.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * Administrator
 * 2022/7/11  18:38
 * community
 */
@Controller
@RequestMapping("/my")
public class MyController {

    @Autowired
    MyService myService;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String testHello() {
        Integer.valueOf("asd");     //模拟服务器内部发生错误：500
        String service = myService.service();
        return "Hello World 牛客社区:" + service;
    }

    @PostMapping("/ajax")
    @ResponseBody
    public String testAjax(String name, int age) {
        HashMap<String, Object> m = new HashMap<>();
        m.put(name, age);
        String jsonString = CommunityUtil.getJSONString(0, "异步请求成功！", m);
        return jsonString;

    }

}
