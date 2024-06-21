package ge.bog.blog.model;

import ge.bog.blog.model.enums.SubscriptionStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class SubscribeRequestResponse {
    @NotEmpty
    private List<Long> usersRequested;
    private SubscriptionStatus status;

}