package ge.bog.blog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class SearchBlogDto {
    private long id;
    private String title;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdated;
    private long views;
    private long likes;
    private String author;
    private List<String> tagNames;
    private boolean isLocked;
}