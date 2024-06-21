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
@Table(name = "Ad")
public class AdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private double rpm;
    @Column(nullable = false)
    private  String company;
    @ManyToMany(mappedBy = "ads")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<BlogEntity> blogs;

}