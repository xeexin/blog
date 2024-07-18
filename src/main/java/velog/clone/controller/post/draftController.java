package velog.clone.controller.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import velog.clone.domain.Blog;
import velog.clone.domain.Post;
import velog.clone.domain.Tag;
import velog.clone.domain.User;
import velog.clone.dto.PostDTO;
import velog.clone.service.BlogService;
import velog.clone.service.PostService;
import velog.clone.service.TagService;
import velog.clone.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class draftController {
    private final UserService userService;
    private final BlogService blogService;
    private final PostService postService;
    private final TagService tagService;

    @GetMapping("/@{username}/draft")
    public String showDraftForm(@PathVariable String username, Model model) {
        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());
        List<Post> draftTrue = postService.findByBlogAndDraftTrue(blog.getId());

        model.addAttribute("user", user);
        model.addAttribute("blog", blog);
        model.addAttribute("draftTrue", draftTrue);
        return "draft/draftHome";
    }

    @GetMapping("/@{username}/editPost/{draftTitle}")
    public String showDraftEditForm(@PathVariable String username, @PathVariable String draftTitle, Model model) {
        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());
        Post post = postService.findByPostTitle(draftTitle);
        List<Tag> tags = post.getTags(); //태그 목록을 가져옴

        model.addAttribute("user", user);
        model.addAttribute("blog", blog);
        model.addAttribute("post", post);
        model.addAttribute("tags", tags); // 태그 목록을 모델에 추가합니다.

        return "draft/draftEditForm";
    }

    @PostMapping("/@{username}/updateDraft/{draftTitle}")
    public String updateDraft(@PathVariable String username, @PathVariable String draftTitle, @ModelAttribute PostDTO postDTO) {

        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());

        Post post = postService.findByBlogAndTitle(blog, draftTitle);
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setDraft(false);

        // 기존 태그 제거
        post.getTags().clear();

        // 새로운 태그 추가
        List<Tag> tags = new ArrayList<>();
        for (String tagName : postDTO.getTags().split(",")) {
            Tag tag = tagService.findByName(tagName).orElseGet(() -> new Tag(tagName));
            tags.add(tag);
        }
        post.setTags(tags);

        // 포스트 저장
        for (Tag tag : tags) {
            tag.setPost(post); // 각 태그에 대해 포스트 설정
        }
        postService.savePost(post); // 업데이트된 포스트 저장

        // URL 인코딩 처리
        String encodedUsername = UriComponentsBuilder.fromPath(username)
                .build()
                .encode()
                .toUriString();

        return "redirect:/";
    }

    @PostMapping("/@{username}/deleteDraft/{draftTitle}")
    public String deleteDraft(@PathVariable String username, @PathVariable String draftTitle) {

        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());

        Post post = postService.findByBlogAndTitle(blog, draftTitle);

        if (post != null && post.isDraft()) {
            postService.deletePost(post.getId());
        }

        // URL 인코딩 처리
        String encodedUsername = UriComponentsBuilder.fromPath(username)
                .build()
                .encode()
                .toUriString();


        return "redirect:/";

    }


}

