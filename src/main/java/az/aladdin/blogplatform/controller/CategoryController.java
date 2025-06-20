package az.aladdin.blogplatform.controller;

import az.aladdin.blogplatform.model.enums.CategoryType;
import az.aladdin.blogplatform.services.abstraction.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryServiceImpl;

    @DeleteMapping
    public ResponseEntity<String> deleteCategory(@RequestParam CategoryType categoryType) {
        categoryServiceImpl.deleteCategory(categoryType);
        return ResponseEntity.ok("Category deleted!");
    }

}
