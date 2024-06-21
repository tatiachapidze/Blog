package ge.bog.blog.util;

import ge.bog.blog.entity.BlogEntity;
import ge.bog.blog.entity.SubscriptionEntity;
import ge.bog.blog.exceptions.*;
import ge.bog.blog.model.enums.SubscriptionStatus;
import ge.bog.blog.repository.UserRepository;
import lombok.*;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class AccessControl {
    private final UserRepository userRepository;

    public boolean checkUserAccessToBlog(BlogEntity blog, long userId) {
        if(blog.isArchived()){
            throw new DeniedAccessException("Blog is archived");
        }
        if(blog.getAuthor().getId() == userId ){
            return true;
        }
        if(blog.getBlockedUsers().contains(userId)){
            throw new DeniedAccessException("User is blocked");
        }
        if (blog.isLocked()) {
            SubscriptionEntity subscription = blog.getSubscriptions().stream()
                    .filter(s -> s.getUser().getId() == userId)
                    .findFirst()
                    .orElseThrow(() -> new DeniedAccessException("You need to subscribe"));

            if (subscription.getStatus() == SubscriptionStatus.REJECTED) {
                throw new DeniedAccessException("User subscription is rejected");
            }

            if (subscription.getStatus() == SubscriptionStatus.REQUESTED) {
                throw new DeniedAccessException("User subscription is not approved yet");
            }
        }
        return true;
    }

    public boolean checkUserOwnershipToBlog(long blogId, long userId) {
        return userRepository.isUserOwnerOrModeratorOfBlog(blogId, userId);
    }
}