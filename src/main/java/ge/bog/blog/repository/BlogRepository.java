package ge.bog.blog.repository;
import ge.bog.blog.entity.BlogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<BlogEntity, Long> {
    Optional<BlogEntity> findByTitle(String title);
    @Query("SELECT COUNT(b) > 0 FROM BlogEntity b WHERE b.id = :blogId AND b.author.id = :authorId")
    boolean checkBlogAuthor(long blogId, long authorId);

    @Query("SELECT DISTINCT b FROM BlogEntity b LEFT JOIN b.tags t " +
            "WHERE (:title IS NULL OR LOWER(b.title) LIKE " +
            "CONCAT('%', coalesce(CAST(:#{#title} AS string),''), '%')) " +
            "AND (:tagNames IS NULL OR LOWER(t.name) IN :tagNames) " +
            "AND b.isArchived = false")
    List<BlogEntity> searchByTitleAndTags(@Param("title") String title, @Param("tagNames") List<String> tagNames);


    List<BlogEntity> findByIsArchivedFalseOrderByViewsDesc();

    @Modifying
    @Query("UPDATE BlogEntity b SET b.isArchived = true WHERE b.lastUpdated < :date")
    void archiveBlogsNotUpdatedSince(@Param("date") LocalDateTime date);

    @Query("SELECT b FROM BlogEntity b JOIN b.viewCounts v WHERE KEY(v) <= :targetDate")
    List<BlogEntity> findBlogsWithOldViews(LocalDate targetDate);

}