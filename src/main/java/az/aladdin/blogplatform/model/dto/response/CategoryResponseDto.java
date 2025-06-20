package az.aladdin.blogplatform.model.dto.response;

import az.aladdin.blogplatform.model.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryResponseDto {
    String categoryId;
    CategoryType categoryType;
}
