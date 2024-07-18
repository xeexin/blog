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

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagService tagService;
    private final SeriesService seriesService;

    public PostDTO convertToDTO(Post post) {

        PostDTO postDTO = new PostDTO();
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setSeriesName(post.getSeriesName());
        postDTO.setTags(post.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.joining(",")));

        return postDTO;
    }

    public Post convertToEntity(PostDTO postDTO, Blog blog) {

        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setBlog(blog);

        // 태그 설정 로직
        String[] tagNames = postDTO.getTags().split(",");
        for (String tagName : tagNames) {
            Tag tag = new Tag(tagName.trim());
            post.addTag(tag);
        }

        // 시리즈 설정 로직
//        if (postDTO.getSeriesName() != null && !postDTO.getSeriesName().isEmpty()) {
//            List<Series> seriesList = seriesService.findByPostId(post.getId());
//            for (Series series : seriesList) {
//                String seriesName = series.getSeriesName();
//
//                if (seriesName == post.getSeries().getSeriesName()) {
//                    series = new Series();
//                    series.setSeriesName(postDTO.getSeriesName());
//                    series.setBlog(blog);
//                    series.setPost(post);
//
//                    post.setSeries(series);
//                }
//            }
//        }

        return post;

    }

    public void updatePost(Long postId, PostDTO postDTO) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());

        // 기존 태그들을 제거하고 새로운 태그들을 추가합니다.
        post.getTags().clear();
        String[] tagNames = postDTO.getTags().split(",");
        for (String tagName : tagNames) {
            Tag tag = new Tag(tagName.trim());
            post.addTag(tag);
        }

        //시리즈
        if (postDTO.getSeriesName() != null && !postDTO.getSeriesName().isEmpty()) {
            Series series = seriesService.findBySeriesName(postDTO.getSeriesName());
            post.setSeriesName(series.getSeriesName());

        }

        postRepository.save(post);
    }

    public Post findByPostTitleAndUser(String postTitle, User user) {
        return postRepository.findByTitleAndBlogUser(postTitle, user)
                .orElseThrow(() -> new IllegalArgumentException("Post Not Found For the User"));
    }



    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    @Transactional
    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Post findByPostId(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("포스트를 찾을 수 없습니다."));
    }

    public Post findByPostTitle(String postTitle) {
        return postRepository.findByTitle(postTitle)
                .orElseThrow(() -> new IllegalArgumentException("포스트를 찾을 수 없습니다."));
    }

    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    public Post findPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public void updateTags(Long postId, String tags) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        post.getTags().clear();

        if (tags != null && !tags.isEmpty()) {
            List<Tag> newTags = tagService.saveTags(tags, post);
            post.getTags().addAll(newTags);
        }

        postRepository.save(post);
    }

    public List<Post> findByBlogAndDraftFalse(Long blogId) {
        return postRepository.findByBlogIdAndDraftFalse(blogId);
    }

    public List<Post> findByBlogAndDraftTrue(Long blodId) {
        return postRepository.findByBlogIdAndDraftTrue(blodId);
    }

    public List<Post> findPublishedPostsByBlogId(Long blogId) {
        return postRepository.findByBlogIdAndDraftFalse(blogId);
    }

    public List<Post> findAllPublishedPosts() {
        return postRepository.findByDraftFalse();
    }

    public Post findByBlogAndTitle(Blog blog, String postTitle) {
        return postRepository.findByBlogAndTitle(blog, postTitle).orElseThrow(() -> new IllegalArgumentException("POST CAN NOT FOUND"));

    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

//    public List<Series> findByUser(User user) {
//        return postRepository.findByUser(user);
//    }

    public List<Post> findByBlog(Blog blog) {
        return postRepository.findByBlogId(blog.getId());
    }

}
