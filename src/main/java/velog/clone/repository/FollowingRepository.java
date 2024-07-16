package velog.clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import velog.clone.domain.Follower;
import velog.clone.domain.Following;
import velog.clone.domain.User;

import java.util.List;

public interface FollowingRepository extends JpaRepository<Following, Long> {
    boolean existsByFollowingAndFollowed(User following, User followed);

    void deleteByFollowingAndFollowed(User following, User followed);

//    List<Following> findByUserId(Long userId);
//    List<Following> findByFollowerId(Long followerId);
}
