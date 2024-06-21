package ge.bog.blog.repository;

import ge.bog.blog.entity.BlogEntity;
import ge.bog.blog.entity.SubscriptionEntity;
import ge.bog.blog.entity.UserEntity;
import ge.bog.blog.model.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    List<SubscriptionEntity> findAllByUserAndBlog(UserEntity user, BlogEntity blog);
    List<SubscriptionEntity> findByBlogIdAndUserIdIn(Long blogId, List<Long> userIds);
    List<SubscriptionEntity> findByBlogIdAndStatus(Long blogId, SubscriptionStatus status);

}