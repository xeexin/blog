package velog.clone.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import velog.clone.Const.SessionConst;
import velog.clone.domain.Blog;
import velog.clone.domain.Post;
import velog.clone.domain.Role;
import velog.clone.domain.User;
import velog.clone.repository.BlogRepository;
import velog.clone.repository.PostRepository;
import velog.clone.repository.UserRepository;
import velog.clone.service.PostService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 홈 페이지 관련 요청을 처리하는 컨트롤러
 * 메인 페이지 조회, 로그인 여부에 따른 다른 페이지 표시를 담당합니다.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    /** 블로그 관련 데이터베이스 작업을 처리하는 레포지토리 */
    private final BlogRepository blogRepository;
    /** 게시글 비즈니스 로직을 처리하는 서비스 */
    private final PostService postService;
    /** 게시글 관련 데이터베이스 작업을 처리하는 레포지토리 */
    private final PostRepository postRepository;

    /**
     * 홈 페이지를 조회하는 메서드
     * 로그인 여부에 따라 다른 페이지를 반환하며, 로그인한 경우 게시글 목록과 페이징 정보를 제공합니다.
     *
     * @param loginUser 세션에 저장된 로그인 사용자 정보
     * @param model 뷰에 전달할 데이터를 담는 모델 객체
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지당 게시글 수 (기본값: 3)
     * @return 로그인 여부에 따른 뷰 이름
     */
    @GetMapping("/")
    public String HomeLogin(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "3") int size) {

        // 로그인하지 않은 경우 메인 인덱스 페이지로 이동
        if (loginUser == null) {
            return "index";
        }

        // 로그인 사용자 정보를 모델에 추가
        model.addAttribute("user", loginUser);

        // 사용자 권한 정보를 문자열로 변환하여 모델에 추가
        String role = loginUser.getRole().toString();
        model.addAttribute("role", role);

        // 페이징을 적용한 게시글 목록 조회
        Page<Post> postPage = postRepository.findAll(PageRequest.of(page, size));

        // 현재 사용자의 블로그 존재 여부 확인
        Optional<Blog> userBlog = blogRepository.findByUserId(loginUser.getId());

        // 블로그 존재 여부에 따른 플래그 설정
        if (userBlog.isPresent()) {
            model.addAttribute("blog", true);
        } else {
            model.addAttribute("blog", false);
        }

        // 발행된 모든 게시글 조회 (참고용 데이터)
        List<Post> allPublishedPosts = postService.findAllPublishedPosts();

        // 페이징 관련 정보와 게시글 목록을 모델에 추가
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("currentPage", page);

        return "loginHome";
    }

}


