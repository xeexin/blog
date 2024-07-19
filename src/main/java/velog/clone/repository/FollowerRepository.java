package velog.clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import velog.clone.domain.Follower;
import velog.clone.domain.User;

import java.util.List;

public interface FollowerRepository extends JpaRepository<Follower, Long> {
    boolean existsByFollowerAndFollowed(User follower, User followed);
    void deleteByFollowerAndFollowed(User follower, User followed);


}
