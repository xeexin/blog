package velog.clone.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 데이터 전송 객체 (Data Transfer Object)
 * 클라이언트와 서버 간에 게시글 정보를 전송하기 위한 DTO 클래스입니다.
 */
@Getter
@Setter
public class PostDTO {

    /** 게시글 제목 */
    private String title;

    /** 게시글 내용 */
    private String content;

    /** 태그들을 콤마로 구분한 문자열 (예: "태그1,태그2,태그3") */
    private String tags;

    /** 게시글이 속한 시리즈 이름 */
    private String seriesName;

}
