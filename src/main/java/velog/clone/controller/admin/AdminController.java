package velog.clone.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;
import velog.clone.Const.SessionConst;
import velog.clone.domain.*;
import velog.clone.service.BlogService;
import velog.clone.service.CommentService;
import velog.clone.service.PostService;
import velog.clone.service.UserService;

import java.util.List;

/**
 * 관리자 기능을 담당하는 컨트롤러
 * 관리자 페이지 조회, 게시글 삭제 등의 관리자 전용 기능을 제공합니다.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    /** 사용자 관련 비즈니스 로직을 처리하는 서비스 */
    private final UserService userService;
    /** 게시글 관련 비즈니스 로직을 처리하는 서비스 */
    private final PostService postService;
    /** 댓글 관련 비즈니스 로직을 처리하는 서비스 */
    private final CommentService commentService;

    /**
     * 관리자 페이지를 조회하는 메서드
     * 모든 발행된 게시글 목록과 사용자 권한 정보를 제공합니다.
     *
     * @param loginUser 세션에 저장된 로그인 사용자 정보
     * @param model 뷰에 전달할 데이터를 담는 모델 객체
     * @return 관리자 페이지 뷰 이름
     */
    @GetMapping("/admin")
    public String showAdmin(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model) {

        // 로그인한 사용자의 상세 정보 조회
        User user = userService.findByUsername(loginUser.getUsername());
        String role = user.getRole().toString();

        // 모든 발행된 게시글 목록 조회
        List<Post> allPublishedPosts = postService.findAllPublishedPosts();

        // 뷰에 전달할 데이터 설정
        model.addAttribute("postList", allPublishedPosts);
        model.addAttribute("role", role);

        return "/admin/admin";
    }

    /**
     * 관리자가 게시글을 삭제하는 메서드
     * 관리자 권한이 있는 경우에만 게시글과 관련 댓글을 모두 삭제합니다.
     *
     * @param postId 삭제할 게시글의 ID
     * @param loginUser 세션에 저장된 로그인 사용자 정보
     * @return 삭제 성공 시 200 OK, 권한 없음 시 403 Forbidden
     */
    @DeleteMapping("/admin/deletePost/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser) {
        // 사용자 정보 조회
        User user = userService.findByUsername(loginUser.getUsername());

        // 관리자 권한 확인
        if (user.getRole() == Role.ADMIN) {
            // 게시글에 달린 모든 댓글 조회
            List<Comment> comments = commentService.findByPostId(postId);

            // 댓글들을 먼저 삭제 (참조 무결성 유지)
            for (Comment comment : comments) {
                commentService.deleteComment(comment);
            }

            // 게시글 삭제
            postService.deletePost(postId);
            return ResponseEntity.ok().build();
        } else {
            // 권한이 없는 경우 403 Forbidden 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
