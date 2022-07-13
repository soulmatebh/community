package com.soulmate.community.controller;

//import com.google.code.kaptcha.Producer;
//import org.apache.commons.lang3.StringUtils;
import com.google.code.kaptcha.Producer;
import com.soulmate.community.entity.User;
import com.soulmate.community.service.UserService;
import com.soulmate.community.util.CommunityConstant;
import com.soulmate.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.core.context.SecurityContextHolder;
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

//    @Autowired
//    private RedisTemplate redisTemplate;

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
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session
         session.setAttribute("kaptcha", text);

//        // 验证码的归属
//        String kaptchaOwner = CommunityUtil.generateUUID();
//        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
//        cookie.setMaxAge(60);
//        cookie.setPath(contextPath);
//        response.addCookie(cookie);
//        // 将验证码存入Redis
//        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
//        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // 将图片输出给浏览器
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            LOGGER.error("响应验证码失败：" + e.getMessage());
        }
    }

    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，本VIP论坛已经向您的邮箱发送了一封激活邮件，请尽快激活！");
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
                    model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了！");
                    model.addAttribute("target", "/login");
                    break;
                }
            case ACTIVATION_REPEAT:
                {
                    model.addAttribute("msg", "无效操作，该账号已经激活过了！");
                    model.addAttribute("target", "/");
                    break;
                }
            case ACTIVATION_FAILED:
                {
                    model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
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
            boolean rememberme,     //记住我选项
            Model model, HttpSession session,
            HttpServletResponse response /*,
            @CookieValue("kaptchaOwner") String kaptchaOwner */) {
        // 检查验证码
        String kaptcha = (String) session.getAttribute("kaptcha");
//        String kaptcha = null;
//        if (StringUtils.isNoneBlank(kaptchaOwner)) {
//            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
//            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
//        }

        if (StringUtils.isBlank(kaptcha)
                || StringUtils.isBlank(code)
                || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确！");
            return "site/login";
        }
        // 检查账号，密码
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
