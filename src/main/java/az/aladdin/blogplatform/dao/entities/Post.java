package az.aladdin.blogplatform.dao.entities;

import az.aladdin.blogplatform.model.enums.PostStatus;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "post")
public class Post {

    @Id
    @Column(unique = true, nullable = false, length = 12)
    private String id;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    private Integer readingTime;

    private Long commentCount = 0L;

    private Long likeCount = 0L;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Category category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    private List<PostLike> postLikes = new ArrayList<>();

    @PrePersist
    public void generatedId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId().substring(0, 12);
        }
        if (readingTime == null) {
            int wordsPerMinute = 200; // Orta oxuma sürəti
            String[] words = content.trim().split("\\s+");
            int wordCount = words.length;
            readingTime = (int) Math.ceil((double) wordCount / wordsPerMinute);
        }

    }

}
