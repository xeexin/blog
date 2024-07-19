package velog.clone.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import velog.clone.domain.Post;
import velog.clone.domain.Tag;
import velog.clone.repository.TagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Transactional
    public Tag saveTag(Tag tag, Post post) {
        tag.setPost(post);
        return tagRepository.save(tag);
    }

    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

    public List<Tag> getTagsFromNames(String tagNames) {
        List<Tag> tags = new ArrayList<>();
        if (tagNames != null && !tagNames.isEmpty()) {
            String[] tagArray = tagNames.split(",");
            List<String> tagNameList = new ArrayList<>();
            for (String tagName : tagArray) {
                tagNameList.add(tagName.trim());
            }
            tags = tagRepository.findByNameIn(tagNameList);
            for (String tagName : tagNameList) {
                if (tags.stream().noneMatch(tag -> tag.getName().equals(tagName))) {
                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    tags.add(saveTag(newTag));
                }
            }
        }
        return tags;
    }

    @Transactional
    public List<Tag> saveTags(String tagNames, Post post) {
        List<Tag> tags = new ArrayList<>();
        if (tagNames != null && !tagNames.isEmpty()) {
            String[] tagArray = tagNames.split(",");
            for (String tagName : tagArray) {
                tagName = tagName.trim();
                Optional<Tag> existingTag = tagRepository.findByName(tagName);
                Tag tag;
                if (existingTag.isPresent()) {
                    tag = existingTag.get();
                } else {
                    tag = new Tag(tagName);
                    tag = saveTag(tag);
                }
                tag.setPost(post);
                tags.add(saveTag(tag, post));
            }
        }
        return tags;
    }
}
