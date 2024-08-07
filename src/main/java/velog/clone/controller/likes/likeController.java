package velog.clone.controller.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.util.UriComponentsBuilder;
import velog.clone.Const.SessionConst;
import velog.clone.domain.Likes;
import velog.clone.domain.Post;
import velog.clone.domain.User;
import velog.clone.repository.LikeRepository;
import velog.clone.repository.PostRepository;
import velog.clone.service.LikeService;
import velog.clone.service.PostService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class likeController {

    private final LikeRepository likeRepository;
    private final PostService postService;

    @PostMapping("/@{username}/post/{postTitle}/like")
    public String like(@PathVariable String username, @PathVariable String postTitle, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser) {

        if (loginUser == null) {
            return "redirect:/login";
        }

        Post post = postService.findByPostTitle(postTitle);

        Optional<Likes> optionalLike = likeRepository.findByPostAndUser(post, loginUser);

        if (optionalLike.isPresent()) {
            likeRepository.delete(optionalLike.get()); // 이미 좋아요 정보가 있는 상태라면 삭제
        } else {
            Likes likes = new Likes();
            likes.setPost(post);
            likes.setUser(loginUser);
            likes.setLikeIt(true);
            likeRepository.save(likes);
        }

        String encodedUsername = UriComponentsBuilder.fromPath(username)
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
