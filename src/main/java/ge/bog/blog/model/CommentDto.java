package ge.bog.blog.model;

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
public class CommentDto {
    private long id;
    private String content;
    private LocalDateTime postedAt;
    private long blogId;
    private String userName;
}