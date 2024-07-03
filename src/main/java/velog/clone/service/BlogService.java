package velog.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@RequiredArgsConstructor
public class BlogService {

    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final FileStore fileStore;
    private final ImgFileRepository imgFileRepository;

    @Transactional
    public void saveBlog(Long userId, Blog blog, MultipartFile profileImgFile) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        blog.setUser(user);

        if (!profileImgFile.isEmpty()) {
            ImgFile imgFile = new ImgFile();
            UploadFile uploadFile = fileStore.storeFile(profileImgFile);
            imgFile.setAttachFile(uploadFile);
            imgFileRepository.save(imgFile);
            blog.setProfileImg(imgFile);
        } else {
            blog.setProfileImg(null);
        }

        blogRepository.save(blog);
    }
}
