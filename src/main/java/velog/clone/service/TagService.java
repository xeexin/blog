package velog.clone.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import velog.clone.domain.Post;
import velog.clone.domain.Tag;
import velog.clone.repository.TagRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional
    public Tag saveTag(Tag tag, Post post) {
        tag.setPost(post);
        return tagRepository.save(tag);
    }

    public List<Tag> findAllTags() {
        return tagRepository.findAll();
    }

    public Tag findTagById(Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid tag Id:" + id));
    }

    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

}
