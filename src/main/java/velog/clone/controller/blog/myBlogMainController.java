package velog.clone.controller.blog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import velog.clone.domain.Blog;
import velog.clone.domain.Post;
import velog.clone.domain.User;
import velog.clone.repository.BlogRepository;
import velog.clone.repository.PostRepository;
import velog.clone.repository.UserRepository;
import velog.clone.service.BlogService;
import velog.clone.service.PostService;
import velog.clone.service.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class myBlogMainController {

    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final PostService postService;


    @GetMapping("/@{username}/blogMain")
    public String showMyBlogMain(@PathVariable("username") String username, Model model) {

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            User existUser = user.get();
            Optional<Blog> blogUser = blogRepository.findByUserId(existUser.getId());

            model.addAttribute("user", existUser);
            if (blogUser.isPresent()) {

                model.addAttribute("blog", blogUser.get());

                List<Post> posts = postService.findByBlogAndDraftFalse(user.get().getBlog().getId());


                model.addAttribute("posts", posts);

                return "/myBlogMain";
            } else {
                model.addAttribute("blog", null);
            }
        }
        return "redirect:/";

    }

}
