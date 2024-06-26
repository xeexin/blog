package velog.clone.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import velog.clone.domain.Likes;
import velog.clone.domain.Post;
import velog.clone.domain.User;
import velog.clone.repository.LikeRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class LikeService {
    private final LikeRepository likeRepository;

    public Long getLikeCnt(Long postId) {
        return likeRepository.countByPostIdAndLikeItTrue(postId);
    }

//    public void likePost(Long postId, Long userId) {
//        Likes like = likeRepository.findByPostIdAndUserId(postId, userId)
//                .orElse(new Likes());
//
//        like.setPost(new Post(postId));
//        like.setUser(new User(userId));
//        like.setLikeIt(true);
//        likeRepository.save(like);
//    }
}
