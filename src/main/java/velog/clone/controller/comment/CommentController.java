package velog.clone.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import velog.clone.Const.SessionConst;
import velog.clone.domain.Comment;
import velog.clone.domain.Post;
import velog.clone.domain.User;
import velog.clone.repository.CommentRepository;
import velog.clone.repository.PostRepository;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @PostMapping("/posts/{postId}/comments")
    public String addComment(@PathVariable Long postId,@RequestParam String reply, Model model, @SessionAttribute(name = SessionConst.LOGIN_USER) User user ) {

        Optional<Post> postOptional = postRepository.findById(postId);

        if (!postOptional.isPresent()) {
            model.addAttribute("error", "포스트가 없습니다.");
            return "redirect:/";
        }

        Comment newComment = new Comment();

        Post post = postOptional.get();
        newComment.setPost(post);
        newComment.setReply(reply);
        newComment.setUser(user);
        commentRepository.save(newComment);

        model.addAttribute("post", post);
        model.addAttribute("newComment", newComment);
        return "redirect:/posts/{postId}";
    }
}
