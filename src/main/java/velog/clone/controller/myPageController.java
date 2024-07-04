package velog.clone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import velog.clone.File.FileStore;
import velog.clone.domain.Blog;
import velog.clone.domain.User;
import velog.clone.repository.BlogRepository;
import velog.clone.repository.UserRepository;

import java.net.MalformedURLException;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class myPageController {
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final FileStore fileStore;

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
}
