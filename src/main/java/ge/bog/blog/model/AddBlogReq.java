package ge.bog.blog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Component
public class AddBlogReq {
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdated;
    @JsonProperty("isLocked")
    private boolean isLocked;
    private int viewCount;
    private long authorId;
    private List<Long> moderatorIds;
    private List<Long> tagIds;
    private List<Long> adIds;
}