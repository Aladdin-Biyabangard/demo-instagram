package az.aladdin.blogplatform.services.impl;

import az.aladdin.blogplatform.configuration.mappers.CategoryMapper;
import az.aladdin.blogplatform.configuration.mappers.PostMapper;
import az.aladdin.blogplatform.dao.entities.Category;
import az.aladdin.blogplatform.dao.entities.Post;
import az.aladdin.blogplatform.dao.repository.CategoryRepository;
import az.aladdin.blogplatform.dao.repository.PostRepository;
import az.aladdin.blogplatform.exception.ResourceNotFoundException;
import az.aladdin.blogplatform.model.dto.response.CategoryResponseDto;
import az.aladdin.blogplatform.model.dto.response.PostResponseShortDto;
import az.aladdin.blogplatform.model.enums.CategoryType;
import az.aladdin.blogplatform.services.abstraction.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    @Cacheable(cacheNames = "categoryType", key = "#categoryType")
    public List<PostResponseShortDto> getCategoryByCategoryType(CategoryType categoryType) {
        log.info("Fetching posts for category type: {}", categoryType);
        Optional<Category> category = categoryRepository.findCategoriesByCategoryType(categoryType);

        if (category.isEmpty()) {
            log.warn("Category not found for category type: {}", categoryType);
            throw new ResourceNotFoundException("Category not found!");
        }

        var posts = category.get().getPosts();
        log.info("Found {} posts for category type: {}", posts.size(), categoryType);
        return postMapper.toShortResponse(posts);
    }

    @Override
    public List<CategoryResponseDto> getMostPopular3Category() {
        log.info("Fetching most popular 3 categories");
        // Burada gələcəkdə loqika əlavə ediləcək
        return List.of();
    }

    @Override
    @Cacheable(cacheNames = "categories")
    public List<CategoryResponseDto> getCategories() {
        log.info("Fetching all categories");
        var categories = categoryRepository.findAll();
        log.info("Found {} categories", categories.size());
        return categoryMapper.toResponse(categories);
    }

    @Override
    public void deleteCategory(CategoryType categoryType) {
        log.info("Deleting category with type: {}", categoryType);
        Category category = categoryRepository.findCategoriesByCategoryType(categoryType)
                .stream()
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Category not found for type: {}", categoryType);
                    return new ResourceNotFoundException("Category not found!");
                });

        List<Post> posts = postRepository.findPostsByCategory_CategoryType(category.getCategoryType());
        log.info("Found {} posts associated with category type {}. Removing category from posts.", posts.size(), categoryType);

        posts.forEach(post -> post.setCategory(null));
        postRepository.saveAll(posts);
        categoryRepository.delete(category);
        log.info("Category {} deleted successfully", categoryType);
    }
}
