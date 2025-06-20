package az.aladdin.blogplatform.dao.repository;

import az.aladdin.blogplatform.dao.entities.Post;
import az.aladdin.blogplatform.dao.entities.User;
import az.aladdin.blogplatform.model.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {

    List<Post> findPostsByCategory_CategoryType(CategoryType type);

    boolean existsPostByUserAndId(User user, String postId);


}
