package velog.clone.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import velog.clone.Const.SessionConst;
import velog.clone.domain.*;
import velog.clone.repository.*;
import velog.clone.service.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final LikeRepository likeRepository;

    private final TagService tagService;
    private final UserService userService;
    private final BlogService blogService;
    private final PostService postService;
    private final CommentService commentService;

    //글 쓰기
    @GetMapping("/{id}/writePost")
    public String showPostForm(@PathVariable Long id, Model model) {

        User user = userService.findById(id);
        Blog blog = blogService.findByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("blog",blog);
        model.addAttribute("post", new Post());

        return "postForm";
    }

    @PostMapping("/{id}/writePost")
    public String savePost(@PathVariable Long id, @ModelAttribute Post post, @RequestParam("tags") String tags, Model model) {

        User user = userService.findById(id);
        Blog blog = blogService.findByUserId(user.getId());


        Post newPost = new Post();
        newPost.setBlog(blog);
        newPost.setDraft(false);
        newPost.setCreatedAt(LocalDateTime.now());
        newPost.setContent(post.getContent());
        newPost.setTitle(post.getTitle());

        postService.savePost(newPost);

        saveTags(tags, newPost);

        model.addAttribute("message", "포스팅 완료");
        return "redirect:/";
    }


    @PostMapping("/{id}/post/draft")
    public String saveDraft( @PathVariable Long id, @ModelAttribute Post post, @RequestParam("tags") String tags, Model model) {

        User user = userService.findById(id);
        Blog blog = blogService.findByUserId(user.getId());

        post.setBlog(blog);
        post.setDraft(true);
        post.setCreatedAt(LocalDateTime.now());
        postService.savePost(post);

        saveTags(tags, post);

        List<String> tagList = Arrays.asList(tags.split(","));
        for (String tagName : tagList) {
            Tag tag = new Tag();
            tag.setName(tagName.trim());
            tag.setPost(post);
            tagService.saveTag(tag);
        }

        model.addAttribute("message", "임시저장 완료");
        return "redirect:/";
    }

    @GetMapping("/posts/{postId}")
    public String viewPost(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, @PathVariable Long postId, Model model) {

        Post post = postService.findByPostId(postId);
        List<Comment> comments = commentService.findByPostId(postId);

        boolean likedByUser = false;
        Long cntLike = null;

        if (loginUser != null) {
            Optional<Likes> like = likeRepository.findByPostAndUser(post, loginUser);
            likedByUser = like.isPresent();

            cntLike = likeRepository.countByPostIdAndLikeItTrue(postId);
        }

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("likedByUser", likedByUser);
        model.addAttribute("cntLike", cntLike);
        return "viewPost";
    }

    private void saveTags(String tags, Post post) {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        List<String> tagList = Arrays.asList(tags.split(","));
        tagList.stream()
                .map(String::trim)
                .distinct()
                .forEach(tagName -> {
                    Tag tag = tagService.findByName(tagName)
                            .orElseGet(() -> {
                                Tag newTag = new Tag();
                                newTag.setName(tagName);
                                return tagService.saveTag(newTag);
                            });
                    tag.setPost(post);
                    tagService.saveTag(tag);
                });
    }
}
