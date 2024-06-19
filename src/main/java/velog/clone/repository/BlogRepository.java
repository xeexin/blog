package velog.clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import velog.clone.domain.Blog;

public interface BlogRepository extends JpaRepository<Blog, Long> {

}
