package velog.clone.controller.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import velog.clone.Const.SessionConst;
import velog.clone.domain.Likes;
import velog.clone.domain.Post;
import velog.clone.domain.User;
import velog.clone.repository.LikeRepository;
import velog.clone.repository.PostRepository;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class likeController {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    @PostMapping("/posts/{postId}/like")
    public String like(@PathVariable Long postId, Model model, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser) {

        if (loginUser == null) {
            return "redirect:/login";
        }

        Optional<Post> optionalPost = postRepository.findById(postId);
        if (!optionalPost.isPresent()) {
            return "redirect:/";
        }

        Post post = optionalPost.get();
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

        return "redirect:/posts/{postId}";
    }

}
