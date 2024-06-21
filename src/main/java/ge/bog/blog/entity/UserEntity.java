package ge.bog.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "BlogUser")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "author")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<BlogEntity> blogs;

    @OneToMany(mappedBy = "user")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<CommentEntity> comments;
}