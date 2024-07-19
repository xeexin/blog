package velog.clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import velog.clone.domain.Likes;
import velog.clone.domain.Post;
import velog.clone.domain.User;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    Long countByPostTitleAndLikeItTrue(String postTitle);

    List<Likes> findByUserId(Long userId);


    Optional<Likes> findByPostAndUser(Post post, User loginUser);


}
