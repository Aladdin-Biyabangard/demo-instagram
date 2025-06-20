package az.aladdin.blogplatform.dao.entities;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "post_likes")
public class PostLike {

    @Id
    @Column(unique = true, nullable = false, length = 12)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    Post post;

    @PrePersist
    public void generatedId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId().substring(0, 12);
        }
    }
}
