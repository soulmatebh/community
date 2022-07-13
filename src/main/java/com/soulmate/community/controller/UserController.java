package com.soulmate.community.controller;

import com.soulmate.community.annotation.LoginRequired;
import com.soulmate.community.entity.User;
import com.soulmate.community.service.UserService;
import com.soulmate.community.util.CommunityUtil;
import com.soulmate.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Administrator
 * 2022/7/13  18:58
 * community
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

//    @Autowired private LikeService likeService;

//    @Autowired private FollowService followService;

//    @Value("${qiniu.key.access}")
//    private String accessKey;
//
//    @Value("${qiniu.key.secret}")
//    private String secretKey;
//
//    @Value("${qiniu.bucket.header.name}")
//    private String headerBucketName;
//
//    @Value("${qiniu.bucket.header.url}")
//    private String headerBucketUrl;

    /**
     * 跳转设置页面
     *
     * @return
     */
    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(Model model) {
        // 上传文件名称
//        String fileName = CommunityUtil.generateUUID();
//        // 设置响应信息
//        StringMap policy = new StringMap();
//        policy.put("returnBody", CommunityUtil.getJSONString(0));
//        // 生成上传凭证
//        Auth auth = Auth.create(accessKey, secretKey);
//        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);
//        model.addAttribute("uploadToken", uploadToken);
//        model.addAttribute("fileName", fileName);

        return "site/setting";
    }

    /**
     * 废弃 提交修改头像请求
     *
     * @param headerImage  :MultipartFile  --->  专门用于文件传输
     * @param model
     * @return
     */
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片！");
            return "site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确！");
            return "site/setting";
        }

        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);       //MultipartFile直接可以写入文件
        } catch (IOException e) {
            LOGGER.error("上传文件失败：", e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }

        // 更新当前用户头像的路径(web访问路径)
        // http://localhost:8080/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    /**
     * 将服务器文件中用户上传的头像返回用户头像（服务器上的文件不能直接访问！！）
     *
     * @param fileName
     * @param response
     */
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);

        try (FileInputStream fis = new FileInputStream(fileName);   //读取服务器文件
             OutputStream os = response.getOutputStream(); ) {      //给response输出文件
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            LOGGER.error("读取文件失败：", e.getMessage());
        }
    }
//
//    @GetMapping("/profile/{userId}")
//    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
//        User user = userService.findUserById(userId);
//        if (user == null) {
//            throw new RuntimeException("该用户不存在");
//        }
//        // 用户
//        model.addAttribute("user", user);
//        // 点赞数量
//        int likeCount = likeService.findUserLikeCount(userId);
//        model.addAttribute("likeCount", likeCount);
//
//        // 关注数量
//        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
//        model.addAttribute("followeeCount", followeeCount);
//
//        // 粉丝数量
//        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
//        model.addAttribute("followerCount", followerCount);
//
//        // 是否已关注
//        boolean hasFollowed = false;
//        if (hostHolder.getUser() != null) {
//            hasFollowed =
//                    followService.hasFollowed(
//                            hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
//        }
//        model.addAttribute("hasFollowed", hasFollowed);
//        return "site/profile";
//    }
//
//    /**
//     * 更新头像路径
//     *
//     * @param fileName
//     * @return
//     */
//    @PostMapping("/header/url")
//    @ResponseBody
//    public String updateHeaderUrl(String fileName) {
//        if (StringUtils.isBlank(fileName)) {
//            return CommunityUtil.getJSONString(1, "文件名不能为空");
//        }
//        String url = headerBucketUrl + "/" + fileName;
//        userService.updateHeader(hostHolder.getUser().getId(), url);
//        return CommunityUtil.getJSONString(0);
//    }
//
    /**
     * 修改密码
     *
     * @param
     * @return
     */
    @PostMapping("/forgetPassword")
    @ResponseBody
    public String updatePassword(String oldPassword, String newPassword) {
        // 验证原始密码是否正确
        if (StringUtils.isBlank(oldPassword)) {
            return "原密码不能为空";
//            return CommunityUtil.getJSONString(1, "原密码不能为空");
        }
        User user = hostHolder.getUser();
        System.out.println(user);
        user = userService.findUserById(user.getId());
        System.out.println(user);
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        System.out.println(oldPassword);
        System.out.println(user.getPassword());
        if (!user.getPassword().equals(oldPassword)) {
            return "原密码错误！";
//            return CommunityUtil.getJSONString(1, "原密码错误！");
        }

        // 验证密码是否为空
//        if (StringUtils.isBlank(newPassword)) {
//            return CommunityUtil.getJSONString(1, "新密码不能为空");
//        }
        // 验证两次密码是否相同
        // 是否与原密码相同
//        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
//        if (user.getPassword().equals(newPassword)) {
//            return CommunityUtil.getJSONString(1, "新密码与原密码不能相同！");
//        }

        //计算加密后的密码
        String secretPassWord = CommunityUtil.md5(newPassword + user.getSalt());
        // 更新密码
        int ret = userService.updatePassword(user.getId(), secretPassWord);
//        if (ret == 0) {
//            return CommunityUtil.getJSONString(1, "更新失败！");
//        }
//        SecurityContextHolder.clearContext();
        // 更新成功返回JSON，状态码为0

//        return CommunityUtil.getJSONString(0);
        return "修改成功！";
    }
}
