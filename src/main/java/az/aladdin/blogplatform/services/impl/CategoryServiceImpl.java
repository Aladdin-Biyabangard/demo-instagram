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
        Optional<Category> category = categoryRepository.findCategoriesByCategoryType(categoryType);
        return postMapper.toShortResponse(category.get().getPosts());
    }

    @Override
    public List<CategoryResponseDto> getMostPopular3Category() {
        return List.of();
    }

    @Override
    @Cacheable(cacheNames = "categories")
    public List<CategoryResponseDto> getCategories() {
        var categories = categoryRepository.findAll();
        return categoryMapper.toResponse(categories);
    }

    @Override
    public void deleteCategory(CategoryType categoryType) {
        Category category = categoryRepository.findCategoriesByCategoryType(categoryType).stream().findFirst().
                orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
        List<Post> posts = postRepository.findPostsByCategory_CategoryType(category.getCategoryType());
        posts.forEach(post -> post.setCategory(null));
        postRepository.saveAll(posts);
        categoryRepository.delete(category);
    }
}
