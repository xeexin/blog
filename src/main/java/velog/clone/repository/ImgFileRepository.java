package velog.clone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import velog.clone.File.UploadFile;
import velog.clone.domain.ImgFile;

public interface ImgFileRepository extends JpaRepository<ImgFile, Long> {
}
