package ge.bog.blog.model;

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
public class ViewBlogDto {
    private long id;
    private String title;
    private String content;
    private String createDate;
    private String lastUpdated;
    private long views;
    private long likes;
    private String authorName;
    private List<String> tagNames;
    private List<ViewAdDto> ads;
    private List<ViewCommentDto> comments;
}