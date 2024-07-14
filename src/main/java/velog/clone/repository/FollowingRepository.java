package velog.clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import velog.clone.domain.Following;
import velog.clone.domain.User;

public interface FollowingRepository extends JpaRepository<Following, Long> {
    boolean existsByFollowingAndFollowed(User following, User followed);

    void deleteByFollowingAndFollowed(User following, User followed);
}
