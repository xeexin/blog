package velog.clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import velog.clone.domain.Blog;
import velog.clone.domain.Post;
import velog.clone.domain.Series;
import velog.clone.domain.User;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, Long> {

    List<Series> findByBlogId(Long blogId);

    Series findBySeriesName(String seriesName);

}
