package velog.clone.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import velog.clone.domain.Comment;
import velog.clone.domain.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByBlogId(Long id);

    Optional<Post> findByTitle(String title);

    Comment save(Comment comment);

}
