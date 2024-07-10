package velog.clone.controller.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import velog.clone.Const.SessionConst;
import velog.clone.domain.*;
import velog.clone.dto.PostDTO;
import velog.clone.repository.*;
import velog.clone.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
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
//        model.addAttribute("post", new Post());
        model.addAttribute("postDTO", new PostDTO());

        return "postForm";
    }

    @PostMapping("/{id}/writePost")
    public String savePost(@PathVariable Long id, @ModelAttribute PostDTO postDTO, Model model) {
        return savePostInternal(id, postDTO, false, model);    }

    @PostMapping("/{id}/post/draft")
    public String saveDraft(@PathVariable Long id, @ModelAttribute PostDTO postDTO, Model model) {
        return savePostInternal(id, postDTO, true, model);
    }

    @GetMapping("/posts/{postId}")
    public String viewPost(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, @PathVariable Long postId, Model model) {

        Post post = postService.findByPostId(postId);
        List<Comment> comments = commentService.findByPostId(postId);
        List<Tag> tags = post.getTags(); // 태그 목록을 가져옵니다.


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
        model.addAttribute("tags", tags); // 태그 목록을 모델에 추가합니다.

        return "viewPost";
    }


    private String savePostInternal(Long id, PostDTO postDTO, boolean isDraft, Model model) {
        User user = userService.findById(id);
        Blog blog = blogService.findByUserId(user.getId());

        Post post = postService.convertToEntity(postDTO, blog);
        post.setDraft(isDraft);
        post.setCreatedAt(LocalDateTime.now());

        postService.savePost(post);

        saveTags(postDTO.getTags(), post);

        model.addAttribute("message", isDraft ? "임시저장 완료" : "포스팅 완료");
        return "redirect:/";
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
                                return tagService.saveTag(newTag,post);
                            });
                    tag.setPost(post);
                    tagService.saveTag(tag,post);
                });
    }

}
