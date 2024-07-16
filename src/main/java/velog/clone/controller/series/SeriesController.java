package velog.clone.controller.series;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import velog.clone.Const.SessionConst;
import velog.clone.domain.Blog;
import velog.clone.domain.Post;
import velog.clone.domain.Series;
import velog.clone.domain.User;
import velog.clone.dto.SeriesDTO;
import velog.clone.repository.BlogRepository;
import velog.clone.service.BlogService;
import velog.clone.service.PostService;
import velog.clone.service.SeriesService;
import velog.clone.service.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SeriesController {
    private final UserService userService;
    private final PostService postService;
    private final BlogService blogService;
    private final SeriesService seriesService;
    private final BlogRepository blogRepository;

    @GetMapping("/@{username}/series")
    public String showSeries(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User currentUser,
                             @PathVariable String username, Model model) {

        User loginUser = userService.findById(currentUser.getId());
        User user = userService.findByUsername(username);
        Optional<Blog> blog = blogRepository.findByUserId(user.getId());

        if (!blog.isPresent()) {
            return "/series/seriesMain";
        }

        List<Post> post = postService.findByBlog(blog.get());

//        List<Series> seriesList = seriesService.getSeriesByPost(post)
        SeriesDTO seriesDTO = new SeriesDTO();

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("user", user);
        model.addAttribute("seriesDTO", seriesDTO);
//        model.addAttribute("seriesList", seriesList);

        return "/series/seriesMain";
    }

    @PostMapping("/@{username}/series")
    public String createSeries(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User currentUser,
                               @PathVariable String username, Model model,@RequestParam Long postId,
                               @RequestParam String seriesName, SeriesDTO seriesDTO) {

        Post post = postService.getPostById(postId);
        seriesService.createSeries(post, seriesName);

        return "redirect:/@{username}/series";
    }
}
