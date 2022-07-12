package com.soulmate.community.controller;

import com.soulmate.community.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Administrator
 * 2022/7/11  18:38
 * community
 */
@Controller
//@RequestMapping("/test")
public class MyController {

    @Autowired
    MyService myService;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String testHello() {
        String service = myService.service();
        return "Hello World 牛客社区:" + service;
    }


}
