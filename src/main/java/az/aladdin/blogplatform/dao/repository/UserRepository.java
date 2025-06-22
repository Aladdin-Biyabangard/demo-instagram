package az.aladdin.blogplatform.dao.repository;

import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.userName = :userName")
    Optional<User> findUserByUserName(@Param("userName") String userName);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String authenticatedEmail, Status status);

    @Query("SELECT COUNT(u) FROM User u JOIN u.following f WHERE f.id = :userId")
    long countFollowersByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(f) FROM User u JOIN u.following f WHERE u.id = :userId")
    long countFollowingByUserId(@Param("userId") String userId);





}
