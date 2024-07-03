package velog.clone.File;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import velog.clone.domain.Blog;

@Getter
@Setter
@NoArgsConstructor
public class UploadFile {

    private String uploadFileName; //고객이 업로드한 파일명

    private String storeFileName; // 서부 내부에서 과리하는 파일명


    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
