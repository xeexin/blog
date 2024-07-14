package velog.clone.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import velog.clone.domain.Follower;
import velog.clone.domain.Following;
import velog.clone.domain.User;
import velog.clone.repository.FollowerRepository;
import velog.clone.repository.FollowingRepository;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowingRepository followingRepository;
    private final FollowerRepository followerRepository;

    @Transactional
    public void follow(User follower, User followed) {
        if (!followingRepository.existsByFollowingAndFollowed(follower, followed)) {
            Following following = new Following();
            following.setFollowed(followed);
            following.setFollowing(follower);
            followingRepository.save(following);

            Follower followerEntity = new Follower();
            followerEntity.setFollower(follower);
            followerEntity.setFollowed(followed);
            followerRepository.save(followerEntity);
        }
    }

    @Transactional
    public void unfollow(User follower, User followed) {

        if (followingRepository.existsByFollowingAndFollowed(follower, followed)) {
            followingRepository.deleteByFollowingAndFollowed(follower, followed);
            followerRepository.deleteByFollowerAndFollowed(follower, followed);
        }
    }

    public boolean isFollowing(User follower, User followed) {
        return followingRepository.existsByFollowingAndFollowed(follower, followed);
    }
}
