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
import velog.clone.File.FileStore;
import velog.clone.domain.Blog;
import velog.clone.domain.User;
import velog.clone.repository.BlogRepository;
import velog.clone.repository.UserRepository;
import velog.clone.service.UserService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class myPageController {
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final FileStore fileStore;
    private final UserService userService;

    @GetMapping("/{id}/myPage")
    public String showMyPage(@PathVariable("id") Long id, Model model) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
            return "redirect:/";
        }

        User user = optionalUser.get();

        Optional<Blog> optionalBlog = blogRepository.findByUserId(user.getId());
        if (!optionalBlog.isPresent()) {
            model.addAttribute("error", "블로그를 찾을 수 없습니다.");
            return "redirect:/";
        }

        Blog blog = optionalBlog.get();
        model.addAttribute("user", user);
        model.addAttribute("blog", blog);


        return "myPage";
    }

    @ResponseBody
    @GetMapping("{id}/{filename}")
    public Resource downloadImg(@PathVariable("filename") String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @GetMapping("/{id}/myPage/edit")
    public String formEditProfile(@PathVariable Long id, Model model) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            model.addAttribute("error", "사용자가 없습니다.");
            return "redirect:/";
        }

        User user = optionalUser.get();
        Optional<Blog> optionalBlog = blogRepository.findByUserId(user.getId());

        if (!optionalBlog.isPresent()) {
            model.addAttribute("error", "블로그가 없습니다.");
            return "redirect:/";
        }
        Blog blog = optionalBlog.get();

        model.addAttribute("user", user);
        model.addAttribute("blog", blog);
        return "editProfile";
    }

    @PostMapping ("/{id}/myPage/edit")
    public String EditProfile(@PathVariable Long id, @RequestParam("username") String username,
                              @RequestParam("title") String title,
                              @RequestParam("profileImg") MultipartFile profileImg, RedirectAttributes attributes) {
        try {
            userService.updateUserProfile(id, username, title, profileImg);
            attributes.addFlashAttribute("message", "프로필이 성공적으로 업데이트되었습니다.");
        } catch (IOException e) {
            attributes.addFlashAttribute("error", "프로필 업데이트 중 오류가 발생했습니다.");
            return "redirect:/{id}/myPage/edit";
        }

        return "redirect:/{id}/myPage";
    }
}
