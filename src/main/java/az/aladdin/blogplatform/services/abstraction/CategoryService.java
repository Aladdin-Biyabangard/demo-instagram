package az.aladdin.blogplatform.services.abstraction;

import az.aladdin.blogplatform.model.dto.response.CategoryResponseDto;
import az.aladdin.blogplatform.model.dto.response.PostResponseShortDto;
import az.aladdin.blogplatform.model.enums.CategoryType;

import java.util.List;

public interface CategoryService {

    List<PostResponseShortDto> getCategoryByCategoryType(CategoryType categoryType);

    List<CategoryResponseDto> getMostPopular3Category();

    List<CategoryResponseDto> getCategories();

    void deleteCategory(CategoryType categoryType);
}
