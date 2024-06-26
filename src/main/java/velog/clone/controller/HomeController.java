package velog.clone.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import velog.clone.Const.SessionConst;
import velog.clone.domain.Blog;
import velog.clone.domain.Post;
import velog.clone.domain.User;
import velog.clone.repository.BlogRepository;
import velog.clone.repository.PostRepository;
import velog.clone.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final BlogRepository blogRepository;
    private final PostRepository postRepository;

    @GetMapping("/")
    public String HomeLogin(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model) {

        if (loginUser == null) {
            return "index";
        }

        model.addAttribute("user", loginUser);

        Optional<Blog> userBlog = blogRepository.findByUserId(loginUser.getId());
        if (userBlog.isPresent()) {

            model.addAttribute("blog", true);

            // 모든 포스팅 글 받기
            List<Post> posts = postRepository.findAll();
            model.addAttribute("posts", posts);

        } else {
            model.addAttribute("blog", false);
        }

        return "loginHome";
    }
}


