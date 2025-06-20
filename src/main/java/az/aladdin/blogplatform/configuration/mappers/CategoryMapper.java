package az.aladdin.blogplatform.configuration.mappers;

import az.aladdin.blogplatform.dao.entities.Category;
import az.aladdin.blogplatform.model.dto.response.CategoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    private final PostMapper postMapper;

    public CategoryResponseDto toResponse(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getCategoryType()
        );
    }

    public List<CategoryResponseDto> toResponse(List<Category> categories) {
        return categories.stream().map(this::toResponse).toList();
    }
}
