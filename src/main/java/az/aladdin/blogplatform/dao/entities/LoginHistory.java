package az.aladdin.blogplatform.dao.entities;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "login_history")
public class LoginHistory {

    @Id
    @Column(unique = true, nullable = false, length = 12)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String city;
    private String country;

    private String device;   // Brauzer və ya cihaz tipi
    private String os;       // Əməliyyat sistemi
    private String browser;  // Brauzer

    @CreationTimestamp
    private LocalDateTime loginTime;

    @PrePersist
    public void generatedId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId().substring(0, 12);
        }
    }
}
