package velog.clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import velog.clone.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //이메일로 유저 찾기.
    Optional<User> findByEmail(String email);


    // 이메일 중복 확인을 위해
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("SELECT f.followed FROM Following f WHERE f.following.username = :username")
    List<User> getFollowingList(@Param("username") String username);

    @Query("SELECT f.follower FROM Follower f WHERE f.followed.username = :username")
    List<User> getFollowerList(@Param("username") String username);

}
