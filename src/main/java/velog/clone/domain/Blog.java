package velog.clone.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 블로그 정보를 나타내는 엔티티 클래스
 * 블로그의 기본 정보, 소유자, 게시글, 시리즈 등의 정보를 포함합니다.
 */
@Entity
@Getter
@Setter
public class Blog {
    /** 블로그의 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 블로그를 소유한 사용자 (일대일 관계) */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 블로그 프로필 이미지 (일대일 관계) */
    @OneToOne
    @JoinColumn(name = "img_file_id")
    private ImgFile profileImg;

    /** 블로그 제목 */
    private String title;

    /** 블로그에 속한 게시글 목록 (일대다 관계) */
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    /** 블로그에 속한 시리즈 목록 (일대다 관계) */
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Series> series = new ArrayList<>();
}
