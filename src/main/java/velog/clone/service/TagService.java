package velog.clone.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import velog.clone.domain.Tag;
import velog.clone.repository.TagRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional
    public Tag saveTag(Tag tag) {
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
}
