package velog.clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import velog.clone.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {



    //이메일로 유저 찾기.
    Optional<User> findByEmail(String email);


    // 이메일 중복 확인을 위해
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

}
