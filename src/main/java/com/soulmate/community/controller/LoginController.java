package com.soulmate.community.controller;

//import com.google.code.kaptcha.Producer;
//import org.apache.commons.lang3.StringUtils;
import com.google.code.kaptcha.Producer;
import com.soulmate.community.entity.User;
import com.soulmate.community.service.UserService;
import com.soulmate.community.util.CommunityConstant;
import com.soulmate.community.util.CommunityUtil;
import com.soulmate.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // ???????????????
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

//        // ??????????????????session
//         session.setAttribute("kaptcha", text);

        // ??????????????????
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // ??????????????????Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // ???????????????????????????
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            LOGGER.error("????????????????????????" + e.getMessage());
        }
    }

    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "??????????????????VIP???????????????????????????????????????????????????????????????????????????");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    /**
     * http://localhost:8080/community/activation/101/code
     *
     * @param model
     * @param userId
     * @param code
     * @return
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(
            Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        switch (result) {
            case ACTIVATION_SUCCESS:
                {
                    model.addAttribute("msg", "?????????????????????????????????????????????????????????");
                    model.addAttribute("target", "/login");
                    break;
                }
            case ACTIVATION_REPEAT:
                {
                    model.addAttribute("msg", "?????????????????????????????????????????????");
                    model.addAttribute("target", "/");
                    break;
                }
            case ACTIVATION_FAILED:
                {
                    model.addAttribute("msg", "????????????????????????????????????????????????");
                    model.addAttribute("target", "/");
                    break;
                }
            default:
        }
        return "site/operate-result";
    }

    @PostMapping("/login")
    public String login(
            String username,
            String password,
            String code,
            boolean rememberme,     //???????????????
            Model model/*, HttpSession session*/,
            HttpServletResponse response ,
            @CookieValue("kaptchaOwner") String kaptchaOwner) {
        // ???????????????
//        String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if (StringUtils.isNoneBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        if (StringUtils.isBlank(kaptcha)
                || StringUtils.isBlank(code)
                || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "?????????????????????");
            return "site/login";
        }
        // ?????????????????????
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        //        System.out.println(rememberme);

        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        String key = "ticket";
        if (map.containsKey(key)) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
//        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}
