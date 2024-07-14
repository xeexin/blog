package velog.clone.controller.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import velog.clone.Const.SessionConst;
import velog.clone.domain.Post;
import velog.clone.domain.User;
import velog.clone.service.BlogService;
import velog.clone.service.FollowService;
import velog.clone.service.PostService;
import velog.clone.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FollowController {

    private final UserService userService;
    private final FollowService followService;
    private final PostService postService;

    @GetMapping("/@{username}/follow")
    public String showFollow(@PathVariable String username, Model model) {

        User user = userService.findByUsername(username);

        model.addAttribute("user", user);

        return "follow/followingHome";
    }

    @PostMapping("/{userId}/{postTitle}/follow")
    public String follow(@PathVariable Long userId, @PathVariable String postTitle, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model) {
        User logininUser = userService.findById(loginUser.getId());
        User followedUser = userService.findById(userId);
        Post post = postService.findByPostTitle(postTitle);


        // User 객체는 현재 로그인한 유저의 정보를 가지고 있어야 합니다.
        followService.follow(logininUser, followedUser);


        String encodedUsername = UriComponentsBuilder.fromPath(followedUser.getUsername())
                .build()
                .encode()
                .toUriString();

        String encodedPostTitle = UriComponentsBuilder.newInstance()
                .pathSegment(post.getTitle())
                .build()
                .encode()
                .toUriString();

        return "redirect:/@" + encodedUsername + "/post" + encodedPostTitle;
    }

    @PostMapping("/{userId}/{postTitle}/unfollow")
    public String unfollow(@PathVariable Long userId,@PathVariable String postTitle, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model) {
        User followedUser = userService.findById(userId);
        Post post = postService.findByPostTitle(postTitle);
        followService.unfollow(loginUser, followedUser);

        String encodedUsername = UriComponentsBuilder.fromPath(followedUser.getUsername())
                .build()
                .encode()
                .toUriString();

        String encodedPostTitle = UriComponentsBuilder.newInstance()
                .pathSegment(post.getTitle())
                .build()
                .encode()
                .toUriString();

        return "redirect:/@" + encodedUsername + "/post" + encodedPostTitle;

    }
}

