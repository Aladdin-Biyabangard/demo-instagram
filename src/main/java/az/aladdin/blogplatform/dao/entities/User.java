package az.aladdin.blogplatform.dao.entities;

import az.aladdin.blogplatform.model.enums.Role;
import az.aladdin.blogplatform.model.enums.Status;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @Column(unique = true, nullable = false, length = 12)
    private String id;

    @Column(unique = true)
    private String userName;

    @Column(name = "first_name", nullable = false, length = 17)
    private String firstName;

    @Column(name = "last_name", length = 17)
    private String lastName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToMany
    @JoinTable(
            name = "follows",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_id")
    )
    private Set<User> following = new HashSet<>();

    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Comment> comments = new ArrayList<>();

    @PrePersist
    public void generatedId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId().substring(0, 12);
        }
    }


//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
//    }

//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
}
