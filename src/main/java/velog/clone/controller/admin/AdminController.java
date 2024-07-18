package velog.clone.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import velog.clone.Const.SessionConst;
import velog.clone.domain.Blog;
import velog.clone.domain.Post;
import velog.clone.domain.User;
import velog.clone.service.BlogService;
import velog.clone.service.PostService;
import velog.clone.service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;
    private final BlogService blogService;
    private final PostService postService;

    @GetMapping("/admin")
    public String showAdmin(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model) {


        List<Post> allPublishedPosts = postService.findAllPublishedPosts();


        model.addAttribute("postList", allPublishedPosts);


        return "/admin/admin";
    }
}
