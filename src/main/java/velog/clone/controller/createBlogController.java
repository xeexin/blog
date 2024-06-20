package velog.clone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import velog.clone.domain.Blog;
import velog.clone.domain.User;
import velog.clone.repository.BlogRepository;
import velog.clone.repository.UserRepository;
import velog.clone.service.UserService;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class createBlogController {

    private final UserRepository userRepository;
    private final BlogRepository blogRepository;


    @GetMapping("/{id}/createBlog")
    public String showCreateBlog(@PathVariable Long id, Model model) {

        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("blog", new Blog());

            return "createBlog";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/{id}/createBlog")
    public String createBlog(@PathVariable Long id, @ModelAttribute Blog blog, RedirectAttributes attributes) {

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            blog.setUser(user.get());
            blogRepository.save(blog);
            attributes.addFlashAttribute("message", "블로그 생성 완료");
            return "redirect:/";
        } else {
            attributes.addFlashAttribute("error", "사용자를 찾을 수 없습니다");
            return "redirect:/";
        }
    }
}
