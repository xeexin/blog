package velog.clone.controller.series;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
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
        User postUser = userService.findByUsername(username);
        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());
        SeriesDTO seriesDTO = new SeriesDTO();
        List<Series> seriesList = seriesService.findByBlogId(blog.getId());

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("postUser", postUser);
        model.addAttribute("user", user);
        model.addAttribute("seriesDTO", seriesDTO);
        model.addAttribute("seriesList", seriesList);

        return "/series/seriesMain";
    }

    @PostMapping("/@{username}/series")
    public String createSeries(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User currentUser,
                               @PathVariable String username, Model model,
                               @RequestParam String seriesName, SeriesDTO seriesDTO) {

        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());

        seriesDTO.setSeriesName(seriesName);

        Series series = new Series();
        series.setSeriesName(seriesDTO.getSeriesName());
        series.setBlog(blog);

        seriesService.save(series);


        // URL 인코딩 처리
        String encodedUsername = UriComponentsBuilder.fromPath(username)
                .build()
                .encode()
                .toUriString();

        return "redirect:/@" + encodedUsername + "/series";
    }

    @PostMapping("/@{username}/series/edit")
    public String editSeries(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User currentUser,
                             @PathVariable String username,
                             @RequestParam Long seriesId,
                             @RequestParam String seriesName) {
        User user = userService.findByUsername(currentUser.getUsername());
        Blog blog = blogService.findByUserId(user.getId());
        List<Series> byBlogId = seriesService.findByBlogId(blog.getId());
        Series series = seriesService.findBySeriesId(seriesId);

        series.setSeriesName(seriesName);
        seriesService.save(series);

        // URL 인코딩 처리
        String encodedUsername = UriComponentsBuilder.fromPath(username)
                .build()
                .encode()
                .toUriString();

        return "redirect:/@" + encodedUsername + "/series";
    }

    @PostMapping("/@{username}/series/delete")
    public String deleteSeries(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User currentUser,
                               @PathVariable String username,
                               @RequestParam Long seriesId) {

        User user = userService.findByUsername(currentUser.getUsername());
        Blog blog = blogService.findByUserId(user.getId());
        List<Series> byBlogId = seriesService.findByBlogId(blog.getId());
        Series series = seriesService.findBySeriesId(seriesId);
        seriesService.deleteSeries(series);

        // URL 인코딩 처리
        String encodedUsername = UriComponentsBuilder.fromPath(username)
                .build()
                .encode()
                .toUriString();

        return "redirect:/@" + encodedUsername + "/series";
    }

}
