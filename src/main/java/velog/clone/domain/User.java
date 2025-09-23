package velog.clone.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 사용자 정보를 나타내는 엔티티 클래스
 * 사용자의 기본 정보, 블로그, 팔로우 관계, 좋아요 등의 정보를 포함합니다.
 */
@Entity
@Getter
@Setter
public class User {
    /** 사용자의 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 사용자명 (필수값) */
    @NotEmpty
    private String username;

    /** 이메일 주소 (필수값) */
    @NotEmpty
    private String email;

    /** 비밀번호 (필수값) */
    @NotEmpty
    private String password;

    /** 사용자가 소유한 블로그 (일대일 관계) */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Blog blog;

    /** 이 사용자를 팔로우하는 사용자들의 목록 (일대다 관계) */
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<Follower> followerList;

    /** 이 사용자가 팔로우하는 사용자들의 목록 (일대다 관계) */
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private List<Following> followingList;

    /** 사용자의 권한 (기본값: BASIC) */
    @Enumerated(EnumType.STRING)
    private Role role = Role.BASIC;

    /** 사용자가 누른 좋아요 목록 (일대다 관계) */
    @OneToMany(mappedBy = "user")
    private List<Likes> likes;

}
