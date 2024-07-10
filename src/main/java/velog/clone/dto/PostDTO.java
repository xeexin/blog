package velog.clone.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDTO {

    private String title;
    private String content;
    private String tags; //태그들을 콤마로 구분한 문자열
}
