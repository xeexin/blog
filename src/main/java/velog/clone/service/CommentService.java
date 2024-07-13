package velog.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import velog.clone.domain.Comment;
import velog.clone.domain.Post;
import velog.clone.repository.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public List<Comment> findByPostId(Long id) {
        return commentRepository.findByPostId(id);
    }

    @Transactional
    public void saveComment(Comment newComment) {
        commentRepository.save(newComment);
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
    }
}
