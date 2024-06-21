package ge.bog.blog.service;

import ge.bog.blog.entity.TagEntity;
import ge.bog.blog.exceptions.AlreadyExistsException;
import ge.bog.blog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    @Transactional
    public long addTag(String name) {
        Optional<TagEntity> existingTag = tagRepository.findByName(name);
        if (existingTag.isPresent()) {
            throw new AlreadyExistsException("Tag with this name already exists");
        }
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(name);
        tagRepository.save(tagEntity);
        return tagEntity.getId();
    }
}