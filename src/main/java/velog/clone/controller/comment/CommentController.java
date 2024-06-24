package velog.clone.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import velog.clone.Const.SessionConst;
import velog.clone.domain.Comment;
import velog.clone.domain.Post;
import velog.clone.domain.User;
import velog.clone.repository.BlogRepository;
import velog.clone.repository.CommentRepository;
import velog.clone.repository.PostRepository;
import velog.clone.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;

    @PostMapping("/post/{postId}/comments")
    public String addComment(@PathVariable Long postId,@RequestParam String reply, Model model) {

        Optional<Post> post = postRepository.findById(postId);

        if (!post.isPresent() ) {
            model.addAttribute("error", "포스트 또는 사용자를 찾을 수 없습니다.");
            return "redirect:/";
        }

        Comment comment = new Comment();
        comment.setPost(post.get());
//        comment.setUser(user.get());
        comment.setReply(reply);
        commentRepository.save(comment);

        return "viewPost";
    }

//    @GetMapping("/post/{postId}")
//    public String viewPost(@PathVariable Long userId, @PathVariable Long postId, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model) {
//
//        Optional<Post> post = postRepository.findById(postId);
//        Optional<User> user = userRepository.findById(loginUser.getId());
//
//        if (!post.isPresent()) {
//            model.addAttribute("error", "포스트를 찾을 수 없습니다.");
//            return "redirect:/";
//        }
//
//        if (!user.isPresent()) {
//            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
//            return "redirect:/";
//        }
//
//        List<Comment> comments = commentRepository.findByPostId(postId);
//
//        model.addAttribute("user", user.get());
//        model.addAttribute("post", post.get());
//        model.addAttribute("comments", comments);
//        return "viewPost";
//    }
}
