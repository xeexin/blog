package velog.clone.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 정보를 나타내는 엔티티 클래스
 * 게시글의 기본 정보, 좋아요, 태그, 시리즈 등의 정보를 포함합니다.
 */
@Entity
@Getter
@Setter
public class Post {
    /** 게시글의 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 게시글이 속한 블로그 (다대일 관계) */
    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    /** 게시글 제목 */
    private String title;

    /** 게시글 내용 */
    private String content;

    /** 임시저장 여부 (true: 임시저장, false: 발행됨) */
    private boolean draft;

    /** 게시글 생성 일시 */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** 게시글에 달린 좋아요 목록 (일대다 관계) */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likes = new ArrayList<>();

    /** 게시글에 연결된 태그 목록 (일대다 관계) */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    /** 게시글이 속한 시리즈 이름 */
    private String seriesName;

    /**
     * 게시글에 태그를 추가하는 메서드
     * 태그 목록에 태그를 추가하고 태그의 게시글 참조를 설정합니다.
     *
     * @param tag 추가할 태그 객체
     */
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.setPost(this);
    }

    /**
     * 게시글에서 태그를 제거하는 메서드
     * 태그 목록에서 태그를 제거하고 태그의 게시글 참조를 해제합니다.
     *
     * @param tag 제거할 태그 객체
     */
    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.setPost(null);
    }

    /**
     * 게시글의 전체 태그 목록을 설정하는 메서드
     * 기존 태그를 모두 제거하고 새로운 태그 목록으로 교체합니다.
     *
     * @param tags 새로 설정할 태그 목록
     */
    public void setTags(List<Tag> tags) {
        this.tags.clear();
        for (Tag tag : tags) {
            this.addTag(tag);
        }
    }
}
