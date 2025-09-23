package velog.clone.controller.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import velog.clone.Const.SessionConst;
import velog.clone.File.FileStore;
import velog.clone.domain.*;
import velog.clone.dto.PostDTO;
import velog.clone.repository.*;
import velog.clone.service.*;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 게시글 관련 기능을 담당하는 컨트롤러
 * 게시글 작성, 조회, 수정, 삭제 및 임시저장 기능을 제공합니다.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    /** 좋아요 관련 데이터베이스 작업을 처리하는 레포지토리 */
    private final LikeRepository likeRepository;

    /** 태그 관련 비즈니스 로직을 처리하는 서비스 */
    private final TagService tagService;
    /** 사용자 관련 비즈니스 로직을 처리하는 서비스 */
    private final UserService userService;
    /** 블로그 관련 비즈니스 로직을 처리하는 서비스 */
    private final BlogService blogService;
    /** 게시글 관련 비즈니스 로직을 처리하는 서비스 */
    private final PostService postService;
    /** 댓글 관련 비즈니스 로직을 처리하는 서비스 */
    private final CommentService commentService;
    /** 팔로우 관련 비즈니스 로직을 처리하는 서비스 */
    private final FollowService followService;
    /** 시리즈 관련 비즈니스 로직을 처리하는 서비스 */
    private final SeriesService seriesService;

    /** 파일 저장 및 관리를 담당하는 유틸리티 클래스 */
    private final FileStore fileStore;


    /**
     * 게시글 작성 폼을 보여주는 메서드
     * 사용자 정보, 블로그 정보, 시리즈 목록을 조회하여 게시글 작성 페이지에 전달합니다.
     *
     * @param username 게시글을 작성할 사용자의 이름
     * @param model 뷰에 전달할 데이터를 담는 모델 객체
     * @return 게시글 작성 폼 뷰 이름
     */
    @GetMapping("/@{username}/writePost")
    public String showPostForm(@PathVariable String username, Model model) {

        // 사용자 정보 조회
        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());

        // 뷰에 전달할 기본 데이터 설정
        model.addAttribute("user", user);
        model.addAttribute("blog",blog);
        model.addAttribute("postDTO", new PostDTO()); // 빈 PostDTO 객체 생성

        // 사용자의 시리즈 목록 조회 및 추가
        List<Series> seriesList = seriesService.findByBlogId(user.getBlog().getId());
        model.addAttribute("seriesList", seriesList);

        return "postForm";
    }

    /**
     * 게시글을 발행하여 저장하는 메서드
     * 임시저장이 아닌 정식 발행된 게시글로 저장합니다.
     *
     * @param username 게시글을 작성한 사용자의 이름
     * @param postDTO 게시글 데이터를 담은 DTO 객체
     * @param model 뷰에 전달할 데이터를 담는 모델 객체
     * @return 저장 후 리다이렉트할 경로
     */
    @PostMapping("/@{username}/writePost")
    public String savePost(@PathVariable String username, @ModelAttribute PostDTO postDTO, Model model) {
        return savePostInternal(username, postDTO, false, model);
    }

    /**
     * 게시글을 임시저장하는 메서드
     * 발행하지 않고 임시저장 상태로 저장합니다.
     *
     * @param username 게시글을 작성한 사용자의 이름
     * @param postDTO 게시글 데이터를 담은 DTO 객체
     * @param model 뷰에 전달할 데이터를 담는 모델 객체
     * @return 저장 후 리다이렉트할 경로
     */
    @PostMapping("/@{username}/post/draft")
    public String saveDraft(@PathVariable String username, @ModelAttribute PostDTO postDTO, Model model) {
        return savePostInternal(username, postDTO, true, model);
    }

    /**
     * 게시글 상세 페이지를 조회하는 메서드
     * 게시글 내용, 댓글, 태그, 좋아요 정보 등을 포함한 상세 정보를 조회합니다.
     *
     * @param username 게시글 작성자의 이름
     * @param loginUser 세션에 저장된 로그인 사용자 정보
     * @param postTitle 조회할 게시글의 제목
     * @param model 뷰에 전달할 데이터를 담는 모델 객체
     * @return 게시글 상세 뷰 이름
     */
    @GetMapping("/@{username}/post/{postTitle}")
    public String viewPost(@PathVariable String username, @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, @PathVariable String postTitle, Model model) {

        // 게시글 및 관련 데이터 조회
        Post post = postService.findByPostTitle(postTitle);
        List<Comment> comments = commentService.findByPostId(post.getId());
        int commentCount = comments.size();
        List<Tag> tags = post.getTags(); // 게시글에 연결된 태그 목록
        String seriesName = post.getSeriesName();

        // 사용자 및 블로그 정보 조회
        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());

        // 게시글 작성자 정보 조회
        User postUser = userService.findById(post.getBlog().getId());

        // 팔로우 여부 확인
        boolean isFollowing = followService.isFollowing(loginUser, postUser);

        // 좋아요 관련 정보 초기화
        boolean likedByUser = false;
        Long cntLike = null;

        // 로그인한 사용자에 대해서만 좋아요 정보 조회
        if (loginUser != null) {
            Optional<Likes> like = likeRepository.findByPostAndUser(post, loginUser);
            likedByUser = like.isPresent(); // 현재 사용자가 좋아요를 눌렀는지 확인

            cntLike = likeRepository.countByPostTitleAndLikeItTrue(postTitle); // 총 좋아요 수
        }

        // 뷰에 전달할 데이터 설정
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new Comment()); // 새 댓글 작성용 빈 객체
        model.addAttribute("likedByUser", likedByUser);
        model.addAttribute("cntLike", cntLike);
        model.addAttribute("tags", tags);
        model.addAttribute("commentCount", commentCount);
        model.addAttribute("blog", blog);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("seriesName", seriesName);

        return "viewPost";
    }

    /**
     * 이미지 파일을 다운로드하는 메서드
     * 업로드된 이미지 파일을 Resource 형태로 반환합니다.
     *
     * @param filename 다운로드할 이미지 파일 이름
     * @return 이미지 파일 Resource 객체
     * @throws MalformedURLException URL 형식이 잘못된 경우 발생하는 예외
     */
    @ResponseBody
    @GetMapping("/{filename}")
    public Resource downloadImg(@PathVariable("filename") String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    /**
     * 게시글 수정 폼을 보여주는 메서드
     * 기존 게시글 데이터를 조회하여 수정 폼에 미리 채워준니다.
     *
     * @param username 게시글 작성자의 이름
     * @param postTitle 수정할 게시글의 제목
     * @param model 뷰에 전달할 데이터를 담는 모델 객체
     * @return 게시글 수정 폼 뷰 이름
     */
    @GetMapping("/@{username}/post/{postTitle}/edit")
    public String editPostForm(@PathVariable String username, @PathVariable String postTitle, Model model) {
        // 사용자 및 게시글 정보 조회
        User user = userService.findByUsername(username);
        Post post = postService.findByPostTitle(postTitle);
        PostDTO postDTO = postService.convertToDTO(post); // 엔티티를 DTO로 변환
        Blog blog = blogService.findByUserId(user.getId());

        // 사용자의 시리즈 목록 조회
        List<Series> seriesList = seriesService.findByBlogId(blog.getId());

        // 게시글에 연결된 태그 목록 조회
        List<Tag> tags = post.getTags();

        // 뷰에 전달할 데이터 설정
        model.addAttribute("user", user);
        model.addAttribute("post", post);
        model.addAttribute("postDTO", postDTO);
        model.addAttribute("tags", tags);
        model.addAttribute("seriesList", seriesList);
        return "editPostForm";
    }

    /**
     * 게시글을 수정하는 메서드
     * 수정된 데이터로 게시글을 업데이트하고 수정된 게시글로 리다이렉트합니다.
     *
     * @param username 게시글 작성자의 이름
     * @param postTitle 수정할 게시글의 기존 제목
     * @param postDTO 수정된 게시글 데이터를 담은 DTO 객체
     * @param model 뷰에 전달할 데이터를 담는 모델 객체
     * @return 수정된 게시글로의 리다이렉트 경로
     */
    @PostMapping("/@{username}/post/{postTitle}/edit")
    public String editPost(@PathVariable String username, @PathVariable String postTitle, @ModelAttribute PostDTO postDTO, Model model) {
        // 기존 게시글 조회 및 업데이트
        Post post = postService.findByPostTitle(postTitle);
        postService.updatePost(post.getId(), postDTO);

        // 수정된 제목 가져오기
        String newPostTitle = postDTO.getTitle();

        // URL 인코딩 처리 (한글 및 특수문자 대응)
        String encodedUsername = UriComponentsBuilder.fromPath(username)
                .build()
                .encode()
                .toUriString();

        String encodedPostTitle = UriComponentsBuilder.newInstance()
                .pathSegment(newPostTitle)
                .build()
                .encode()
                .toUriString();

        return "redirect:/@" + encodedUsername + "/post" + encodedPostTitle;
    }

    /**
     * 게시글을 삭제하는 메서드
     * 게시글과 관련된 댓글들을 모두 삭제한 후 게시글을 삭제합니다.
     *
     * @param username 게시글 작성자의 이름
     * @param postTitle 삭제할 게시글의 제목
     * @return 삭제 후 리다이렉트할 경로
     */
    @PostMapping("/@{username}/post/{postTitle}/delete")
    public String deletePost(@PathVariable String username, @PathVariable String postTitle) {
        // 사용자와 게시글 정보 조회
        User user = userService.findByUsername(username);
        Post post = postService.findByPostTitleAndUser(postTitle, user);

        // 게시글에 달린 모든 댓글 삭제 (참조 무결성 유지)
        List<Comment> comments = commentService.findByPostId(post.getId());
        for (Comment comment : comments) {
            commentService.deleteComment(comment);
        }

        // 게시글 삭제
        postService.deletePost(post.getId());

        return "redirect:/";
    }


    /**
     * 게시글 저장을 수행하는 내부 메서드
     * 임시저장과 정식 발행을 공통으로 처리합니다.
     *
     * @param username 게시글을 작성한 사용자의 이름
     * @param postDTO 게시글 데이터를 담은 DTO 객체
     * @param isDraft 임시저장 여부 (true: 임시저장, false: 정식 발행)
     * @param model 뷰에 전달할 데이터를 담는 모델 객체
     * @return 저장 후 리다이렉트할 경로
     */
    private String savePostInternal(String username, PostDTO postDTO, boolean isDraft, Model model) {
        // 사용자 및 블로그 정보 조회
        User user = userService.findByUsername(username);
        Blog blog = blogService.findByUserId(user.getId());

        // DTO를 엔티티로 변환
        Post post = postService.convertToEntity(postDTO, blog);

        // 게시글 속성 설정
        post.setDraft(isDraft); // 임시저장 여부 설정
        post.setCreatedAt(LocalDateTime.now()); // 생성 시간 설정

        // 시리즈 설정
        setSeriesForPost(postDTO, blog, post);

        // 게시글 저장
        postService.savePost(post);

        // 태그 저장
        saveTags(postDTO.getTags(), post);

        // URL 인코딩 처리 (한글 및 특수문자 대응)
        String encodedUsername = UriComponentsBuilder.fromPath(username)
                .build()
                .encode()
                .toUriString();

        String encodedPostTitle = UriComponentsBuilder.newInstance()
                .pathSegment(post.getTitle())
                .build()
                .encode()
                .toUriString();

        // 성공 메시지 설정
        model.addAttribute("message", isDraft ? "임시저장 완료" : "포스팅 완료");

        // 임시저장이면 홈으로, 정식 발행이면 게시글 상세 페이지로 이동
        if (isDraft) {
            return "redirect:/";
        }
        return "redirect:/@" + encodedUsername + "/post" + encodedPostTitle;
    }

    /**
     * 게시글에 태그를 저장하는 내부 메서드
     * 콤마로 구분된 태그 문자열을 개별 태그로 분리하여 저장합니다.
     *
     * @param tags 콤마로 구분된 태그 문자열
     * @param post 태그가 연결될 게시글 엔티티
     */
    private void saveTags(String tags, Post post) {

        // 태그가 없는 경우 처리 종료
        if (tags == null || tags.isEmpty()) {
            return;
        }

        // 콤마로 구분된 태그를 리스트로 변환
        List<String> tagList = Arrays.asList(tags.split(","));
        tagList.stream()
                .map(String::trim) // 공백 제거
                .distinct() // 중복 제거
                .forEach(tagName -> {
                    String finalTagName = tagName;
                    // 기존 태그 조회 또는 새 태그 생성
                    Tag tag = tagService.findByName(tagName)
                            .orElseGet(() -> {
                                Tag newTag = new Tag();
                                newTag.setName(finalTagName);
                                return tagService.saveTag(newTag,post);
                            });
                    // 태그와 게시글 연결 설정 및 저장
                    tag.setPost(post);
                    tagService.saveTag(tag,post);
                });
    }

    /**
     * 게시글에 시리즈를 설정하는 내부 메서드
     * DTO에서 시리즈 이름을 가져와 게시글에 설정합니다.
     *
     * @param postDTO 게시글 데이터를 담은 DTO 객체
     * @param blog 블로그 엔티티
     * @param post 시리즈가 설정될 게시글 엔티티
     */
    private void setSeriesForPost(PostDTO postDTO, Blog blog, Post post) {
        // 시리즈 이름이 있는 경우에만 설정
        if (postDTO.getSeriesName() != null && !postDTO.getSeriesName().isEmpty()) {
            post.setSeriesName(postDTO.getSeriesName());
        }
    }
}
