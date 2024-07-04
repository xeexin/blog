package velog.clone.controller.blog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import velog.clone.File.FileStore;
import velog.clone.controller.Img.ImgForm;
import velog.clone.domain.Blog;
import velog.clone.File.UploadFile;
import velog.clone.domain.ImgFile;
import velog.clone.domain.User;
import velog.clone.repository.UserRepository;
import velog.clone.service.BlogService;
import velog.clone.service.UserService;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CreateBlogController {

    private final UserRepository userRepository;
    private final BlogService blogService;
    private final UserService userService;

    @GetMapping("/{id}/createBlog")
    public String showCreateBlog(@PathVariable Long id, Model model) {

        User user = userService.findById(id);

        model.addAttribute("user", user);
        model.addAttribute("blog", new Blog());
        model.addAttribute("imgForm", new ImgForm());

        return "createBlog";
    }

    @PostMapping("/{id}/createBlog")
    public String createBlog(@PathVariable("id") Long id, @ModelAttribute Blog blog, @ModelAttribute ImgForm imgForm, RedirectAttributes attributes) {

        try {
            blogService.saveBlog(id, blog, imgForm.getAttachFile());

            attributes.addFlashAttribute("message", "블로그 생성 완료");

            return "myPage";

        } catch (IOException e) {
            log.error("Failed to create blog", e);
            attributes.addFlashAttribute("error", "블로그 생성 중 오류 발생");
            return "redirect:/";
        }

    }
}
