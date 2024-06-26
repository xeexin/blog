package velog.clone.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 한개의 포스트에 좋아요 여러개

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 한명의 유저가 좋아요 여러개 할 수 있음

    private boolean likeIt;  //좋아요

}
