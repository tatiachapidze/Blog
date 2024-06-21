package ge.bog.blog.model;

import ge.bog.blog.model.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class SubscriptionDto {
    private long id;
    private long userId;
    private long blogId;
    private SubscriptionStatus status;
}