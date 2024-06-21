
package ge.bog.blog.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Component
public class UpdateBlogReq {
    private long blogId;
    private long userId;
    private String title;
    private String content;
    @JsonProperty("isLocked")
    private boolean isLocked;
    private List<Long> moderatorIds;
    private Set<Long> blockedUserIds;
    private List<Long> tagIds;
    private List<Long> adIds;

}