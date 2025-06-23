package az.aladdin.blogplatform.dao.entities;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "blocks")
public class Block {

    @Id
    @Column(unique = true, nullable = false, length = 12)
    private String id;

    @ManyToOne
    @JoinColumn(name = "blocker_id", nullable = false)
    private User blocker; // kim bloklayır

    @ManyToOne
    @JoinColumn(name = "blocked_id", nullable = false)
    private User blocked; // kim bloklanır

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @PrePersist
    public void generatedId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId().substring(0, 12);
        }
    }
}
