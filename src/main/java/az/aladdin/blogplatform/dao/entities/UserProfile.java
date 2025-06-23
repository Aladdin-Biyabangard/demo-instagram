package az.aladdin.blogplatform.dao.entities;

import az.aladdin.blogplatform.model.enums.user.Gender;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfile {

    @Id
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(length = 245)
    private String bio;

    @Column(length = 500)
    private String profilePhotoUrl;

    private String profilePhotoKey;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @PrePersist
    public void generatedId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId().substring(0, 12);
        }
    }

}
