package velog.clone.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import velog.clone.File.UploadFile;

@Entity
@Getter
@Setter
@Table(name = "img_file")
public class ImgFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private UploadFile attachFile;

}
