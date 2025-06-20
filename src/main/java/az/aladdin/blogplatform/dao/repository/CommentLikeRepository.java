package az.aladdin.blogplatform.dao.repository;

import az.aladdin.blogplatform.dao.entities.Comment;
import az.aladdin.blogplatform.dao.entities.CommentLike;
import az.aladdin.blogplatform.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, String> {

    Optional<CommentLike> findByUserAndComment(User user, Comment comment);
}
