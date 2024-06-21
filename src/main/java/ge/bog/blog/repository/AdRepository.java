package ge.bog.blog.repository;

import ge.bog.blog.entity.AdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdRepository extends JpaRepository<AdEntity, Long> {
    Optional<AdEntity> findByTitle(String title);
}