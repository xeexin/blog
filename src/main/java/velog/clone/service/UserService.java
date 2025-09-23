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

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 사용자 조회, 프로필 수정, 팔로우 관리, 회원 탈퇴 등의 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    /** 사용자 관련 데이터베이스 작업을 처리하는 레포지토리 */
    private final UserRepository userRepository;
    /** 블로그 관련 데이터베이스 작업을 처리하는 레포지토리 */
    private final BlogRepository blogRepository;
    /** 파일 저장 및 관리를 담당하는 유틸리티 클래스 */
    private final FileStore fileStore;
    /** 이미지 파일 관련 데이터베이스 작업을 처리하는 레포지토리 */
    private final ImgFileRepository imgFileRepository;
    /** 게시글 관련 데이터베이스 작업을 처리하는 레포지토리 */
    private final PostRepository postRepository;
    /** 댓글 관련 데이터베이스 작업을 처리하는 레포지토리 */
    private final CommentRepository commentRepository;


    /**
     * 사용자명으로 사용자를 조회하는 메서드
     *
     * @param username 조회할 사용자의 이름
     * @return 조회된 사용자 엔티티
     * @throws UsernameNotFoundExeption 사용자를 찾을 수 없는 경우 발생하는 예외
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundExeption("User not found"));
    }

    /**
     * 사용자 ID로 사용자를 조회하는 메서드
     *
     * @param id 조회할 사용자의 ID
     * @return 조회된 사용자 엔티티
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우 발생하는 예외
     */
    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
    }

    /**
     * 사용자가 팔로우하고 있는 사용자 목록을 조회하는 메서드
     *
     * @param user 팔로잉 목록을 조회할 사용자
     * @return 팔로잉 중인 사용자 목록
     */
    public List<User> getFollowingList(User user) {
        return userRepository.getFollowingList(user.getUsername());
    }

    /**
     * 사용자를 팔로우하고 있는 사용자 목록을 조회하는 메서드
     *
     * @param user 팔로워 목록을 조회할 사용자
     * @return 팔로워 사용자 목록
     */
    public List<User> getFollowerList(User user) {
        return userRepository.getFollowerList(user.getUsername());
    }

    /**
     * 사용자 프로필을 업데이트하는 메서드
     * 사용자명, 블로그 제목, 프로필 이미지를 수정합니다.
     *
     * @param userId 수정할 사용자의 ID
     * @param username 새로운 사용자명
     * @param title 새로운 블로그 제목
     * @param profileImg 새로운 프로필 이미지 파일
     * @throws IOException 파일 업로드 중 오류가 발생한 경우
     */
    @Transactional
    public void updateUserProfile(Long userId, String username, String title, MultipartFile profileImg) throws IOException {
        // 사용자와 블로그 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        Blog blog = blogRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("블로그를 찾을 수 없습니다"));

        // 기본 정보 업데이트
        user.setUsername(username);
        blog.setTitle(title);

        // 프로필 이미지 처리
        if (!profileImg.isEmpty()) {
            // 새로운 이미진 업로드 및 저장
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

        // 변경사항 저장
        userRepository.save(user);
        blogRepository.save(blog);
    }

    /**
     * 사용자 회원 탈퇴를 처리하는 메서드
     * 사용자와 관련된 모든 데이터(댓글, 좋아요, 게시글, 블로그)를 삭제합니다.
     *
     * @param username 탈퇴할 사용자의 이름
     */
    @Transactional
    public void secessionUser(String username) {
        // 사용자를 찾아서 삭제 처리
        Optional<User> userOptional = userRepository.findByUsername(username);
        userOptional.ifPresent(user -> {

            // 사용자가 작성한 댓글 삭제
            List<Comment> comments = commentRepository.findByUserId(user.getId());
            commentRepository.deleteAll(comments);

            // 사용자가 누른 좋아요 삭제
            user.getLikes().clear();

            // 사용자 블로그 및 관련 데이터 삭제
            Blog blog = user.getBlog();
            if (blog != null) {
                // 블로그의 모든 게시글과 관련된 댓글 삭제
                List<Post> posts = postRepository.findByBlogId(blog.getId());
                for (Post post : posts) {
                    List<Comment> postComments = commentRepository.findByPostId(post.getId());
                    commentRepository.deleteAll(postComments);
                }
                // 블로그의 모든 게시글 삭제
                postRepository.deleteAll(posts);

                // 블로그 삭제
                blogRepository.delete(blog);
            }

            // 최종적으로 사용자 삭제
            userRepository.delete(user);
        });
    }
}
