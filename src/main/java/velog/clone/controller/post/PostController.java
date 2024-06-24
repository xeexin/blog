package velog.clone.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import velog.clone.Const.SessionConst;
import velog.clone.domain.Blog;
import velog.clone.domain.Comment;
import velog.clone.domain.Post;
import velog.clone.domain.User;
import velog.clone.repository.BlogRepository;
import velog.clone.repository.CommentRepository;
import velog.clone.repository.PostRepository;
import velog.clone.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    //글 쓰기
    @GetMapping("/{id}/writePost")
    public String showPostForm(@PathVariable Long id, Model model) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
            return "redirect:/";
        }

        Optional<Blog> blogUser = blogRepository.findByUserId(user.get().getId());
        if (!blogUser.isPresent()) {
            model.addAttribute("error", "블로그를 찾을 수 없습니다");
            return "redirect:/";
        }

        model.addAttribute("user", user.get());
        model.addAttribute("blog", blogUser.get());
        model.addAttribute("post", new Post());

        return "postForm";
    }

    @PostMapping("/{id}/writePost")
    public String savePost(@PathVariable Long id, @ModelAttribute Post post, Model model) {

        Optional<User> user = userRepository.findById(id);

        if (!user.isPresent()) {
            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
            return "redirect:/";
        }

        Optional<Blog> blogUser = blogRepository.findByUserId(user.get().getId());
        if (!blogUser.isPresent()) {
            model.addAttribute("error", "블로그를 찾을 수 없습니다.");
            return "redirect:/";
        }

        Post newPost = new Post();
        newPost.setBlog(blogUser.get());
        newPost.setDraft(false);
        newPost.setCreatedAt(LocalDateTime.now());
        newPost.setContent(post.getContent());
        newPost.setTitle(post.getTitle());
        postRepository.save(newPost);

        model.addAttribute("message", "포스팅 완료");
        return "redirect:/";
    }

    @PostMapping("/{id}/post/draft")
    public String saveDraft(@PathVariable Long id, @ModelAttribute Post post, Model model) {

        Optional<User> user = userRepository.findById(id);
        model.addAttribute("message", "임시저장 완료");
        return "redirect:/";
    }

    @GetMapping("/posts/{postId}")
    public String viewPost(@PathVariable Long postId, Model model) {
        Optional<Post> post = postRepository.findById(postId);

        if (!post.isPresent()  ) {
            model.addAttribute("error", "포스트 또는 사용자를 찾을 수 없습니다.");
            return "redirect:/";
        }


        model.addAttribute("post", post.get());
        return "viewPost";
    }

    @GetMapping("/user/{userId}/posts/{postId}")
    public String viewPost(@PathVariable Long userId, @PathVariable Long postId, Model model) {
        Optional<Post> post = postRepository.findById(postId);
        Optional<User> user = userRepository.findById(userId);

        if (!post.isPresent()) {
            model.addAttribute("error", "포스트를 찾을 수 없습니다.");
            return "redirect:/";
        }

        if (!user.isPresent()) {
            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
            return "redirect:/";
        }

        model.addAttribute("user", user.get());
        model.addAttribute("post", post.get());
        return "viewPost";
    }
}
