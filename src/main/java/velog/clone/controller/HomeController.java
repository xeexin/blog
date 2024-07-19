package velog.clone.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import velog.clone.Const.SessionConst;
import velog.clone.domain.Blog;
import velog.clone.domain.Post;
import velog.clone.domain.Role;
import velog.clone.domain.User;
import velog.clone.repository.BlogRepository;
import velog.clone.repository.PostRepository;
import velog.clone.repository.UserRepository;
import velog.clone.service.PostService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    private final BlogRepository blogRepository;
    private final PostService postService;
    private final PostRepository postRepository;

    @GetMapping("/")
    public String HomeLogin(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "3") int size) {

        if (loginUser == null) {
            return "index";
        }

        model.addAttribute("user", loginUser);

        String role = loginUser.getRole().toString();
        model.addAttribute("role", role);

        Page<Post> postPage = postRepository.findAll(PageRequest.of(page, size));

        Optional<Blog> userBlog = blogRepository.findByUserId(loginUser.getId());

        if (userBlog.isPresent()) {

            model.addAttribute("blog", true);

        } else {
            model.addAttribute("blog", false);
        }

        List<Post> allPublishedPosts = postService.findAllPublishedPosts();
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("currentPage", page);

        return "loginHome";
    }

}


