package ge.bog.blog.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class AddCommentReq {
    @NotEmpty
    private String content;
    private LocalDateTime postedAt;
    @Positive
    private long userId;
    @Positive
    private long blogId;
}