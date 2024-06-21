package ge.bog.blog.repository;

import ge.bog.blog.entity.AdEntity;
import ge.bog.blog.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
    List<TagEntity> findAllByIdIn(List<Long> ids);
    Optional<TagEntity> findByName(String name);
}