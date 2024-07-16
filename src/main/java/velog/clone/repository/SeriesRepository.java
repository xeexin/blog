package velog.clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import velog.clone.domain.Post;
import velog.clone.domain.Series;
import velog.clone.domain.User;

import java.util.List;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    List<Series> findByPost(Post post);

}
