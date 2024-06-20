package velog.clone.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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


    private String title;
    private String content;
    private boolean draft;
    private LocalDateTime createdAt;

}
