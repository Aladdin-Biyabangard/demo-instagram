package az.aladdin.blogplatform.dao.repository;

import az.aladdin.blogplatform.dao.entities.Comment;
import az.aladdin.blogplatform.dao.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {

    boolean existsCommentByUser_IdAndId(String userId, String commentId);

    @Query("SELECT c.post FROM Comment c WHERE c.id=:commentId")
    Post findPostByCommentId(String commentId);

    List<Comment> findCommentsByParentId(String parentCommentId);

    void deleteCommentsByParentId(String parentId);
}
