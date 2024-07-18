package velog.clone.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class OtherUserController {

    private final UserService userService;
    private final BlogService blogService;
    private final PostService postService;

    @GetMapping("/user/@{username}/home")
    public String showOtherUserHome(@PathVariable String username,
                                    @SessionAttribute(name = SessionConst.LOGIN_USER, required = true) User currentUser, Model model) {

        User loginUser = userService.findByUsername(currentUser.getUsername());

        User blogUser = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(blogUser.getId());
        List<Post> postList = postService.findByBlog(blog);

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("user", blogUser);
        model.addAttribute("blog", blog);
        model.addAttribute("postList", postList);

        return "/user/otherUserHome";
    }
}
