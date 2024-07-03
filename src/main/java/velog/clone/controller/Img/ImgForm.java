package velog.clone.controller.Img;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ImgForm {
    private Long id;
    private MultipartFile attachFile;
}
