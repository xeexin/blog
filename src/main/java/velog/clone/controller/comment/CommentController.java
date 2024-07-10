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
import velog.clone.service.CommentService;
import velog.clone.service.PostService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    @PostMapping("/@{username}/posts/{postTitle}/comments")
    public String addComment(@PathVariable String postTitle, @RequestParam String reply, Model model, @SessionAttribute(name = SessionConst.LOGIN_USER) User user ) {

        Post post = postService.findByPostTitle(postTitle);

        Comment newComment = new Comment();

        newComment.setPost(post);
        newComment.setReply(reply);
        newComment.setUser(user);
        commentService.saveComment(newComment);

        model.addAttribute("post", post);
        model.addAttribute("newComment", newComment);
        return "redirect:/@{username}/posts/{postTitle}";
    }
}
