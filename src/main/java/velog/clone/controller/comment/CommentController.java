package velog.clone.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import velog.clone.Const.SessionConst;
import velog.clone.domain.Comment;
import velog.clone.domain.Post;
import velog.clone.domain.User;
import velog.clone.dto.CommentDTO;
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

    @PostMapping("/@{username}/post/{postTitle}/comments")
    public String addComment(@PathVariable String postTitle, @RequestParam String reply, Model model, @SessionAttribute(name = SessionConst.LOGIN_USER) User user) {

        Post post = postService.findByPostTitle(postTitle);

        Comment newComment = new Comment();

        newComment.setPost(post);
        newComment.setReply(reply);
        newComment.setUser(user);
        commentService.saveComment(newComment);

        model.addAttribute("post", post);
        model.addAttribute("newComment", newComment);
        return "redirect:/@{username}/post/{postTitle}";
    }


    @GetMapping("/@{username}/post/{postTitle}/editComment/{commentId}")
    public String showEditCommentForm(@PathVariable String username, @PathVariable String postTitle, @PathVariable Long commentId, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model) {
        Comment comment = commentService.findById(commentId);

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setReply(comment.getReply());

        model.addAttribute("commentDTO", commentDTO);
        model.addAttribute("postTitle", postTitle);
        model.addAttribute("username", username);
        model.addAttribute("commentId", commentId);

        return "comment/editCommentForm";

    }

    @PostMapping("/@{username}/post/{postTitle}/editComment/{commentId}")
    public String editComment(@PathVariable String username, @PathVariable String postTitle, @PathVariable Long commentId, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, @ModelAttribute CommentDTO commentDTO) {
        Comment comment = commentService.findById(commentId);

        comment.setReply(commentDTO.getReply());
        commentService.saveComment(comment);


        //URL 인코딩
        String encodedUsername = UriComponentsBuilder.fromPath(username)
                .build()
                .encode()
                .toUriString();

        String encodedPostTitle = UriComponentsBuilder.newInstance()
                .pathSegment(postTitle)
                .build()
                .encode()
                .toUriString();

        return "redirect:/@" + encodedUsername + "/post" + encodedPostTitle;
    }
}

