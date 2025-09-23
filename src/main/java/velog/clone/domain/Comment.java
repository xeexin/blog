package velog.clone.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 댓글 정보를 나타내는 엔티티 클래스
 * 댓글 내용, 작성자, 게시글 등의 정보를 포함합니다.
 */
@Entity
@Getter
@Setter
public class Comment {
    /** 댓글의 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 댓글이 달린 게시글 (다대일 관계) */
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /** 댓글 작성자 (다대일 관계) */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 댓글 내용 */
    private String reply;

    /** 댓글 작성 일시 */
    @CreationTimestamp
    private LocalDateTime createdAt;

}
