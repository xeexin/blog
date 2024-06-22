package velog.clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import velog.clone.domain.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByBlogId(Long blogId);
}
