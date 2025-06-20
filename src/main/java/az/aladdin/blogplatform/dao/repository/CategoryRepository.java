package az.aladdin.blogplatform.dao.repository;

import az.aladdin.blogplatform.dao.entities.Category;
import az.aladdin.blogplatform.model.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {

    Optional<Category> findCategoriesByCategoryType(CategoryType type);
}
