package az.aladdin.blogplatform.dao.repository;

import az.aladdin.blogplatform.dao.entities.PostLike;
import az.aladdin.blogplatform.dao.entities.Post;
import az.aladdin.blogplatform.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, String> {

    Optional<PostLike> findByUserAndPost(User user, Post post);



}
