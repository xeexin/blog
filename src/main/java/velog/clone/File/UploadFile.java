package velog.clone.File;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import velog.clone.domain.Blog;

/**
 * 업로드된 파일 정보를 담는 클래스
 * 원본 파일명과 서버에 저장된 파일명을 관리합니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class UploadFile {

    /** 사용자가 업로드한 원본 파일명 */
    private String uploadFileName;

    /** 서버 내부에서 관리하는 저장 파일명 (UUID를 사용하여 중복 방지) */
    private String storeFileName;

    /**
     * UploadFile 생성자
     * 원본 파일명과 저장 파일명을 설정합니다.
     *
     * @param uploadFileName 원본 파일명
     * @param storeFileName 저장 파일명
     */
    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
