package velog.clone.controller.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class draftController {
    private final UserService userService;
    private final BlogService blogService;
    private final PostService postService;

    @GetMapping("/@{username}/draft")
    public String showDraftForm(@PathVariable String username, Model model) {
        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());
        List<Post> draftTrue = postService.findByBlogAndDraftTrue(blog.getId());

        model.addAttribute("user", user);
        model.addAttribute("blog", blog);
        model.addAttribute("draftTrue",draftTrue );


        return "draft/draftHome";
    }


}
