package ge.bog.blog.repository;

import ge.bog.blog.entity.AdEntity;
import ge.bog.blog.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
}