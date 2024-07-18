package velog.clone.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import velog.clone.domain.*;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByBlogId(Long id);

    Optional<Post> findByBlogAndTitle(Blog blog, String postTitle);

    Optional<Post> findByTitle(String title);

    Comment save(Comment comment);

    Optional<Post> findByTitleAndBlogUser(String postTitle, User user);

    List<Post> findByBlogIdAndDraftFalse(Long blogId);

    List<Post> findByBlogIdAndDraftTrue(Long blogId);

    List<Post> findByDraftFalse();

}
