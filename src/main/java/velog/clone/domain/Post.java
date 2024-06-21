package velog.clone.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="blog_id",nullable = false)
    private Blog blog;


    private String title; // 포스트 타이틀
    private String content; //포스트 내용
    private boolean draft;  //임시저장 여부

    @CreationTimestamp
    private LocalDateTime createdAt; // 생성일자

}
