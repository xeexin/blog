package velog.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import velog.clone.domain.Comment;
import velog.clone.domain.Post;
import velog.clone.repository.CommentRepository;

import java.util.List;

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
}
