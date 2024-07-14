package velog.clone.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import velog.clone.File.FileStore;
import velog.clone.File.UploadFile;
import velog.clone.domain.Blog;
import velog.clone.domain.ImgFile;
import velog.clone.domain.User;
import velog.clone.repository.BlogRepository;
import velog.clone.repository.ImgFileRepository;
import velog.clone.repository.UserRepository;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final FileStore fileStore;
    private final ImgFileRepository imgFileRepository;


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
}
