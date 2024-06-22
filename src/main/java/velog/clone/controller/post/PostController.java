package velog.clone.controller.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import velog.clone.domain.Blog;
import velog.clone.domain.Post;
import velog.clone.domain.User;
import velog.clone.repository.BlogRepository;
import velog.clone.repository.PostRepository;
import velog.clone.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final PostRepository postRepository;

    //글 쓰기
    @GetMapping("/{id}/writingPost")
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

    @PostMapping("/{id}/writingPost")
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
        newPost.setDraft(true);
        newPost.setCreatedAt(LocalDateTime.now());
        newPost.setContent(post.getContent());
        newPost.setTitle(post.getTitle());
        postRepository.save(newPost);

        model.addAttribute("message", "임시저장 완료");
        return "redirect:/";
    }
}
