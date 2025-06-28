package az.aladdin.blogplatform.dao.repository;

import az.aladdin.blogplatform.dao.entities.Post;
import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.dao.entities.UserPostIgnore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPostIgnoreRepository extends JpaRepository<UserPostIgnore, String> {
    boolean existsByUserAndPost(User user, Post post);
}
