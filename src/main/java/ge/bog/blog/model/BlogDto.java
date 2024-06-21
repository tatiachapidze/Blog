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
public class BlogDto {
    private long id;
    private String title;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdated;
    private boolean isLocked;
    private boolean isArchived;
    private long views;
    private long likes;
    private UserDto author;
    private List<TagDto> tags;
    private List<AdDto> ads;
    private List<UserDto> moderators;
    private List<CommentDto> comments;
    private List<SubscriptionDto> subscriptions;

}





