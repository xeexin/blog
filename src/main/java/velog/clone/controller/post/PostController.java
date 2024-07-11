package velog.clone.controller.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import velog.clone.Const.SessionConst;
import velog.clone.domain.*;
import velog.clone.dto.PostDTO;
import velog.clone.repository.*;
import velog.clone.service.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    @GetMapping("/@{username}/writePost")
    public String showPostForm(@PathVariable String username, Model model) {

        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("blog",blog);
        model.addAttribute("postDTO", new PostDTO());

        return "postForm";
    }

    @PostMapping("/@{username}/writePost")
    public String savePost(@PathVariable String username, @ModelAttribute PostDTO postDTO, Model model) {
        return savePostInternal(username, postDTO, false, model);    }

    @PostMapping("/@{username}/post/draft")
    public String saveDraft(@PathVariable String username, @ModelAttribute PostDTO postDTO, Model model) {
        return savePostInternal(username, postDTO, true, model);
    }

    @GetMapping("/@{username}/post/{postTitle}")
    public String viewPost(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, @PathVariable String postTitle, Model model) {

        Post post = postService.findByPostTitle(postTitle);
        List<Comment> comments = commentService.findByPostId(post.getId());
        List<Tag> tags = post.getTags(); // 태그 목록을 가져옵니다.

        boolean likedByUser = false;
        Long cntLike = null;

        if (loginUser != null) {
            Optional<Likes> like = likeRepository.findByPostAndUser(post, loginUser);
            likedByUser = like.isPresent();

            cntLike = likeRepository.countByPostTitleAndLikeItTrue(postTitle);
        }

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("likedByUser", likedByUser);
        model.addAttribute("cntLike", cntLike);
        model.addAttribute("tags", tags); // 태그 목록을 모델에 추가합니다.

        return "viewPost";
    }

    @GetMapping("/@{username}/post/{postTitle}/edit")
    public String editPostForm(@PathVariable String username, @PathVariable String postTitle, Model model) {
        User user = userService.findByUsername(username);
        Post post = postService.findByPostTitle(postTitle);
        PostDTO postDTO = postService.convertToDTO(post);

//        List<Tag> tags = post.getTags(); // 태그 목록을 가져옵니다.


        model.addAttribute("user", user);
        model.addAttribute("post", post);
        model.addAttribute("postDTO", postDTO);
//        model.addAttribute("tags", tags);

        return "editPostForm";
    }

    @PostMapping("/@{username}/post/{postTitle}/edit")
    public String editPost(@PathVariable String username, @PathVariable String postTitle, @ModelAttribute PostDTO postDTO, Model model) {
        Post post = postService.findByPostTitle(postTitle);

        postService.updatePost(post.getId(), postDTO);

        // 새 태그 목록으로 업데이트
//        postService.updateTags(post.getId(), postDTO.getTags());

        String newPostTitle = postDTO.getTitle();

        String encodedUsername = UriComponentsBuilder.fromPath(username)
                .build()
                .encode()
                .toUriString();

        String encodedPostTitle = UriComponentsBuilder.newInstance()
                .pathSegment(newPostTitle)
                .build()
                .encode()
                .toUriString();

        return "redirect:/@" + encodedUsername + "/post" + encodedPostTitle;
    }

    @PostMapping("/@{username}/post/{postTitle}/delete")
    public String deletePost(@PathVariable String username, @PathVariable String postTitle) {
        User user = userService.findByUsername(username);
        Post post = postService.findByPostTitleAndUser(postTitle, user);

        String encodedUsername = UriComponentsBuilder.fromPath(username)
                .build()
                .encode()
                .toUriString();

        postService.deletePost(post.getId());
        return "redirect:/@" + encodedUsername + "/blogMain";
    }


    private String savePostInternal(String username, PostDTO postDTO, boolean isDraft, Model model) {
        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());

        Post post = postService.convertToEntity(postDTO, blog);
        post.setDraft(isDraft);
        post.setCreatedAt(LocalDateTime.now());

        postService.savePost(post);

        saveTags(postDTO.getTags(), post);

        // URL 인코딩 처리
        String encodedUsername = UriComponentsBuilder.newInstance()
                .pathSegment(username)
                .build()
                .encode()
                .toUriString();


        String encodedPostTitle = UriComponentsBuilder.newInstance()
                .pathSegment(post.getTitle())
                .build()
                .encode()
                .toUriString();

        model.addAttribute("message", isDraft ? "임시저장 완료" : "포스팅 완료");
        return "redirect:/@" + encodedUsername + "/post" + encodedPostTitle;
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
                    String finalTagName = tagName;
                    Tag tag = tagService.findByName(tagName)
                            .orElseGet(() -> {
                                Tag newTag = new Tag();
                                newTag.setName(finalTagName);
                                return tagService.saveTag(newTag,post);
                            });
                    tag.setPost(post);
                    tagService.saveTag(tag,post);
                });
    }

}
