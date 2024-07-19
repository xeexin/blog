package velog.clone.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import velog.clone.File.FileStore;
import velog.clone.File.UploadFile;
import velog.clone.domain.*;
import velog.clone.repository.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final FileStore fileStore;
    private final ImgFileRepository imgFileRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundExeption("User not found"));
    }

    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
    }

    public List<User> getFollowingList(User user) {
        return userRepository.getFollowingList(user.getUsername());
    }

    public List<User> getFollowerList(User user) {
        return userRepository.getFollowerList(user.getUsername());
    }

    @Transactional
    public void updateUserProfile(Long userId, String username, String title, MultipartFile profileImg) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        Blog blog = blogRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("블로그를 찾을 수 없습니다"));

        user.setUsername(username);
        blog.setTitle(title);

        if (!profileImg.isEmpty()) {
            ImgFile imgFile = new ImgFile();
            UploadFile uploadFile = fileStore.storeFile(profileImg);
            imgFile.setAttachFile(uploadFile);
            imgFileRepository.save(imgFile);
            blog.setProfileImg(imgFile);
        }else {
            // 기본 이미지 설정
            ImgFile defaultImgFile = new ImgFile();
            UploadFile defaultUploadFile = new UploadFile("user.png", "user.png");
            defaultImgFile.setAttachFile(defaultUploadFile);
            imgFileRepository.save(defaultImgFile);
            blog.setProfileImg(defaultImgFile);
        }

        userRepository.save(user);
        blogRepository.save(blog);
    }

    @Transactional
    public void secessionUser(String username) {
        // 사용자를 찾아서 삭제
        Optional<User> userOptional = userRepository.findByUsername(username);
        userOptional.ifPresent(user -> {

            // 댓글 삭제
            List<Comment> comments = commentRepository.findByUserId(user.getId());
            commentRepository.deleteAll(comments);

            // 좋아요 삭제
            user.getLikes().clear(); // 예시: 사용자가 누른 모든 좋아요를 삭제하는 경우


            // 블로그 삭제
            Blog blog = user.getBlog();
            if (blog != null) {
                // 포스트와 관련된 댓글 삭제
                List<Post> posts = postRepository.findByBlogId(blog.getId());
                for (Post post : posts) {
                    List<Comment> postComments = commentRepository.findByPostId(post.getId());
                    commentRepository.deleteAll(postComments);
                }
                postRepository.deleteAll(posts);

                // 블로그 삭제
                blogRepository.delete(blog);
            }

            // 사용자 삭제
            userRepository.delete(user);
        });
    }
}
