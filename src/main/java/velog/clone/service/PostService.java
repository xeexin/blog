package velog.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import velog.clone.domain.*;
import velog.clone.dto.PostDTO;
import velog.clone.repository.PostRepository;
import velog.clone.repository.TagRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 게시글 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 게시글 조회, 저장, 수정, 삭제 및 DTO 변환 등의 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class PostService {

    /** 게시글 관련 데이터베이스 작업을 처리하는 레포지토리 */
    private final PostRepository postRepository;
    /** 태그 관련 비즈니스 로직을 처리하는 서비스 */
    private final TagService tagService;
    /** 시리즈 관련 비즈니스 로직을 처리하는 서비스 */
    private final SeriesService seriesService;

    /**
     * 게시글 엔티티를 DTO로 변환하는 메서드
     *
     * @param post 변환할 게시글 엔티티
     * @return 변환된 PostDTO 객체
     */
    public PostDTO convertToDTO(Post post) {

        PostDTO postDTO = new PostDTO();
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setSeriesName(post.getSeriesName());
        // 태그 목록을 콤마로 구분된 문자열로 변환
        postDTO.setTags(post.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.joining(",")));

        return postDTO;
    }

    /**
     * PostDTO를 게시글 엔티티로 변환하는 메서드
     *
     * @param postDTO 변환할 PostDTO 객체
     * @param blog 게시글이 속할 블로그 엔티티
     * @return 변환된 게시글 엔티티
     */
    public Post convertToEntity(PostDTO postDTO, Blog blog) {

        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setBlog(blog);

        // 콤마로 구분된 태그 문자열을 개별 태그로 분리하여 설정
        String[] tagNames = postDTO.getTags().split(",");
        for (String tagName : tagNames) {
            Tag tag = new Tag(tagName.trim()); // 공백 제거 후 태그 생성
            post.addTag(tag);
        }

        return post;
    }

    /**
     * 게시글을 업데이트하는 메서드
     * 제목, 내용, 태그, 시리즈 정보를 업데이트합니다.
     *
     * @param postId 업데이트할 게시글의 ID
     * @param postDTO 업데이트할 데이터를 담은 DTO 객체
     * @throws IllegalArgumentException 유효하지 않은 게시글 ID인 경우
     */
    public void updatePost(Long postId, PostDTO postDTO) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        // 기본 정보 업데이트
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());

        // 기존 태그들을 제거하고 새로운 태그들을 추가
        post.getTags().clear();
        String[] tagNames = postDTO.getTags().split(",");
        for (String tagName : tagNames) {
            Tag tag = new Tag(tagName.trim());
            post.addTag(tag);
        }

        // 시리즈 설정 (시리즈가 있는 경우에만)
        if (postDTO.getSeriesName() != null && !postDTO.getSeriesName().isEmpty()) {
            Series series = seriesService.findBySeriesName(postDTO.getSeriesName());
            post.setSeriesName(series.getSeriesName());
        }

        postRepository.save(post);
    }

    /**
     * 제목과 사용자로 게시글을 조회하는 메서드
     *
     * @param postTitle 조회할 게시글의 제목
     * @param user 게시글 작성자
     * @return 조회된 게시글 엔티티
     * @throws IllegalArgumentException 해당 사용자의 게시글을 찾을 수 없는 경우
     */
    public Post findByPostTitleAndUser(String postTitle, User user) {
        return postRepository.findByTitleAndBlogUser(postTitle, user)
                .orElseThrow(() -> new IllegalArgumentException("Post Not Found For the User"));
    }

    /**
     * 게시글을 저장하는 메서드
     *
     * @param post 저장할 게시글 엔티티
     * @return 저장된 게시글 엔티티
     */
    @Transactional
    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    /**
     * 제목으로 게시글을 조회하는 메서드
     *
     * @param postTitle 조회할 게시글의 제목
     * @return 조회된 게시글 엔티티
     * @throws IllegalArgumentException 게시글을 찾을 수 없는 경우
     */
    public Post findByPostTitle(String postTitle) {
        return postRepository.findByTitle(postTitle)
                .orElseThrow(() -> new IllegalArgumentException("포스트를 찾을 수 없습니다."));
    }

    /**
     * 게시글을 삭제하는 메서드
     *
     * @param id 삭제할 게시글의 ID
     */
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    /**
     * 게시글의 태그를 업데이트하는 메서드
     *
     * @param postId 태그를 업데이트할 게시글의 ID
     * @param tags 새로운 태그 문자열 (콤마로 구분)
     * @throws IllegalArgumentException 유효하지 않은 게시글 ID인 경우
     */
    @Transactional
    public void updateTags(Long postId, String tags) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        // 기존 태그 제거
        post.getTags().clear();

        // 새로운 태그 추가 (태그가 있는 경우에만)
        if (tags != null && !tags.isEmpty()) {
            List<Tag> newTags = tagService.saveTags(tags, post);
            post.getTags().addAll(newTags);
        }

        postRepository.save(post);
    }

    /**
     * 특정 블로그의 발행된 게시글 목록을 조회하는 메서드
     *
     * @param blogId 블로그 ID
     * @return 발행된 게시글 목록
     */
    public List<Post> findByBlogAndDraftFalse(Long blogId) {
        return postRepository.findByBlogIdAndDraftFalse(blogId);
    }

    /**
     * 특정 블로그의 임시저장된 게시글 목록을 조회하는 메서드
     *
     * @param blodId 블로그 ID
     * @return 임시저장된 게시글 목록
     */
    public List<Post> findByBlogAndDraftTrue(Long blodId) {
        return postRepository.findByBlogIdAndDraftTrue(blodId);
    }

    /**
     * 모든 발행된 게시글 목록을 조회하는 메서드
     *
     * @return 발행된 모든 게시글 목록
     */
    public List<Post> findAllPublishedPosts() {
        return postRepository.findByDraftFalse();
    }

    /**
     * 특정 블로그에서 제목으로 게시글을 조회하는 메서드
     *
     * @param blog 게시글을 찾을 블로그 엔티티
     * @param postTitle 조회할 게시글의 제목
     * @return 조회된 게시글 엔티티
     * @throws IllegalArgumentException 게시글을 찾을 수 없는 경우
     */
    public Post findByBlogAndTitle(Blog blog, String postTitle) {
        return postRepository.findByBlogAndTitle(blog, postTitle)
                .orElseThrow(() -> new IllegalArgumentException("POST CAN NOT FOUND"));
    }

    /**
     * 특정 블로그의 모든 게시글 목록을 조회하는 메서드
     *
     * @param blog 게시글을 조회할 블로그 엔티티
     * @return 해당 블로그의 모든 게시글 목록
     */
    public List<Post> findByBlog(Blog blog) {
        return postRepository.findByBlogId(blog.getId());
    }

}
