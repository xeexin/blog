package velog.clone.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 파일 저장 및 관리를 담당하는 유틸리티 클래스
 * 업로드된 파일을 서버에 저장하고 관리하는 기능을 제공합니다.
 */
@Component
public class FileStore {

    /** 파일 저장 디렉토리 경로 (application.properties에서 설정) */
    @Value("${file.dir}")
    private String fileDir;

    /**
     * 파일명으로 전체 경로를 반환하는 메서드
     *
     * @param fileName 파일명
     * @return 전체 파일 경로
     */
    public String getFullPath(String fileName) {
        return fileDir + fileName;
    }

    /**
     * 업로드된 파일을 서버에 저장하는 메서드
     * 원본 파일명과 UUID를 사용한 저장 파일명을 생성합니다.
     *
     * @param multipartFile 업로드된 파일 객체
     * @return UploadFile 객체 (원본 파일명과 저장 파일명 포함)
     * @throws IOException 파일 저장 중 오류가 발생한 경우
     */
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        // 빈 파일인 경우 null 반환
        if (multipartFile.isEmpty()) {
            return null;
        }

        // 원본 파일맀과 저장용 파일명 생성
        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        // 파일을 지정된 경로에 저장
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        return new UploadFile(originalFilename, storeFileName);
    }


    /**
     * UUID를 사용하여 저장용 파일명을 생성하는 비공개 메서드
     * 파일명 충돌을 방지하기 위해 UUID를 사용합니다.
     *
     * @param originalFileName 원본 파일명
     * @return UUID와 확장자로 구성된 새로운 파일명
     */
    private String createStoreFileName(String originalFileName) {
        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    /**
     * 파일명에서 확장자를 추출하는 비공개 메서드
     *
     * @param originalFileName 원본 파일명
     * @return 파일 확장자 (점 제외)
     */
    private String extractExt(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos + 1);
    }

}
