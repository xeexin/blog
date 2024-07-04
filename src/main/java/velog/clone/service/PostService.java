package velog.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import velog.clone.domain.Post;
import velog.clone.repository.PostRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Post findByPostId(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("포스트를 찾을 수 없습니다."));
    }

    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    public Post findPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
