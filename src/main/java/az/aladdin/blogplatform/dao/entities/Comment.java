package az.aladdin.blogplatform.dao.entities;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

    @Id
    @Column(unique = true, nullable = false, length = 12)
    private String id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    //Sonradan commente de like mexanizmasi ve cavab verme elave edilecek
    private Long commentLikes = 0L;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String parentId;

    @PrePersist
    public void generatedId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId().substring(0, 12);
        }
    }
}
