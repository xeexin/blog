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

}
