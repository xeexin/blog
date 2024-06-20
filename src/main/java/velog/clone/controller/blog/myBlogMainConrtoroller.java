package velog.clone.controller.blog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import velog.clone.domain.Blog;
import velog.clone.domain.User;
import velog.clone.repository.BlogRepository;
import velog.clone.repository.UserRepository;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class myBlogMainConrtoroller {

    private final UserRepository userRepository;
    private final BlogRepository blogRepository;

    @GetMapping("/{id}/blogMain")
    public String showMyBlogMain(@PathVariable("id") Long id, Model model) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            User existUser = user.get();
            Optional<Blog> blogUser = blogRepository.findByUserId(existUser.getId());

            model.addAttribute("user", existUser);

            if (blogUser.isPresent()) {
                model.addAttribute("blog", blogUser.get());
            } else {
                model.addAttribute("blog", null);
            }
            return "/myBlogMain";
        }
        return "redirect:/";

    }

}
