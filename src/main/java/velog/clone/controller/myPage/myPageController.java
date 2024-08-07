package velog.clone.controller.myPage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import velog.clone.Const.SessionConst;
import velog.clone.File.FileStore;
import velog.clone.domain.Blog;
import velog.clone.domain.User;
import velog.clone.service.BlogService;
import velog.clone.service.UserService;

import java.io.IOException;
import java.net.MalformedURLException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class myPageController {
    private final FileStore fileStore;

    private final UserService userService;
    private final BlogService blogService;


    @GetMapping("/@{username}/myPage")
    public String showMyPage(@PathVariable("username") String username, Model model) {

        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("blog", blog);

        return "myPage";
    }

    @ResponseBody
    @GetMapping("{id}/{filename}")
    public Resource downloadImg(@PathVariable("filename") String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @GetMapping("/@{username}/myPage/edit")
    public String formEditProfile(@PathVariable String username, Model model) {

        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("blog", blog);

        return "editProfile";
    }

    @PostMapping ("/@{username}/myPage/edit")
    public String EditProfile(@RequestParam("username") String username,
                              @RequestParam("title") String title,
                              @RequestParam("profileImg") MultipartFile profileImg, RedirectAttributes attributes,
                              @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser) {

        User currentUser = userService.findByUsername(loginUser.getUsername());

        try {
            userService.updateUserProfile(currentUser.getId(), username, title, profileImg);
            attributes.addFlashAttribute("message", "프로필이 성공적으로 업데이트되었습니다.");
        } catch (IOException e) {
            attributes.addFlashAttribute("error", "프로필 업데이트 중 오류가 발생했습니다.");
            return "redirect:/{id}/myPage/edit";
        }

        String encodedUsername = UriComponentsBuilder.fromPath(username)
                .build()
                .encode()
                .toUriString();

        return "redirect:/@" + encodedUsername + "/myPage";

    }

    @PostMapping("/@{username}/myPage/secession")
    public String userSecession(@PathVariable String username, Model model) {
        userService.secessionUser(username);

        return "redirect:/logout";
    }

}
