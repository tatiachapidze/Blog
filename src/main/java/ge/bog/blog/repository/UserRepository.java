package ge.bog.blog.repository;

import ge.bog.blog.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserName(String userName);
    Optional<UserEntity> findByEmail(String email);
    @Query("SELECT u.id FROM UserEntity u WHERE u.id IN :userIds")
    Set<Long> findExistingIds(@Param("userIds") Set<Long> userIds);

    @Query("SELECT COUNT(b) > 0 FROM BlogEntity b JOIN b.moderators m WHERE b.id = :blogId AND (b.author.id = :userId OR m.id = :userId)")
    boolean isUserOwnerOrModeratorOfBlog(@Param("blogId") long blogId, @Param("userId") long userId);

}