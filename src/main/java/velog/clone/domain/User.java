package velog.clone.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String username;
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;

    //추가예정
//    private String profileImage;

    // 한명의 유저는 하나의 블로그를 가질 수 있다.
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Blog blog;

    // 한명의 유저는 여러명의 팔로워를 가질 수 있다.
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<Follower> followerList;

    // 한명의 유저는 여러명을 팔로잉 할 수 있다.
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private List<Following> followingList;

    @Enumerated(EnumType.STRING)
    private Role role = Role.BASIC;

}
