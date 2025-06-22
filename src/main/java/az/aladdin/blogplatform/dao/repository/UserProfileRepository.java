package az.aladdin.blogplatform.dao.repository;

import az.aladdin.blogplatform.dao.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

//    @Query("SELECT " +
//            "up.id," +
//            " up.bio, up.profilePhotoUrl," +
//            "up.profilePhotoKey FROM UserProfile  up WHERE up.user.id=:userId")
//    UserProfileProjection findByUserId(String userId);

    Optional<UserProfile> findByUserId(String userId);
}
