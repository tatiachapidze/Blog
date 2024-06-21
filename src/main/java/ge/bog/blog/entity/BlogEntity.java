package ge.bog.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Setter
@Getter
@Entity
@Table(name = "Blog")
public class BlogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private LocalDateTime createDate;
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
    @Column(nullable = false)
    private boolean isLocked;
    @Column(nullable = false)
    private boolean isArchived;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name = "blog_tags",
            joinColumns = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Fetch(value = FetchMode.SUBSELECT)
    private List<TagEntity> tags;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<CommentEntity> comments;

    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name = "blog_ad",
            joinColumns = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "ad_id")
    )
    @Fetch(value = FetchMode.SUBSELECT)
    private List<AdEntity> ads;

    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name = "blog_moderators",
            joinColumns = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Fetch(value = FetchMode.SUBSELECT)
    private List<UserEntity> moderators;

    @ElementCollection
    @CollectionTable(name = "blog_views", joinColumns = @JoinColumn(name = "blog_id"))
    @MapKeyColumn(name = "view_date")
    @Column(name = "view_count")
    private Map<LocalDate, Integer> viewCounts = new HashMap<>();

    private long views;
    private long likes;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.DETACH)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<SubscriptionEntity> subscriptions;

    @ElementCollection
    @CollectionTable(name = "blog_likes", joinColumns = @JoinColumn(name = "blog_id"))
    @Column(name = "user_id")
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<Long> likedByUsers = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "blocked_users", joinColumns = @JoinColumn(name = "blog_id"))
    @Column(name = "user_id")
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<Long> blockedUsers = new HashSet<>();
}