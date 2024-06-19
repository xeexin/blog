package velog.clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import velog.clone.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

}
