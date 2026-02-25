package ru.practicum.ewm.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepo;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_shouldReturnCategoryDto() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("Test Category");
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        when(categoryRepo.save(any(Category.class))).thenReturn(category);

        CategoryDto result = categoryService.createCategory(newCategoryDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Category", result.getName());
        verify(categoryRepo, times(1)).save(any(Category.class));
    }

    @Test
    void deleteCategory_shouldCallRepository() {
        Long categoryId = 1L;

        categoryService.deleteCategory(categoryId);

        verify(categoryRepo, times(1)).deleteById(categoryId);
    }

    @Test
    void updateCategory_whenCategoryExists_shouldReturnUpdatedCategory() {
        Long categoryId = 1L;
        NewCategoryDto updateDto = new NewCategoryDto("Updated Category");
        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Old Category");

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepo.save(any(Category.class))).thenReturn(existingCategory);

        CategoryDto result = categoryService.updateCategory(categoryId, updateDto);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Updated Category", result.getName());
        verify(categoryRepo, times(1)).findById(categoryId);
        verify(categoryRepo, times(1)).save(existingCategory);
    }

    @Test
    void updateCategory_whenCategoryNotFound_shouldThrowNotFoundException() {
        Long categoryId = 1L;
        NewCategoryDto updateDto = new NewCategoryDto("Updated Category");

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.updateCategory(categoryId, updateDto));
        verify(categoryRepo, times(1)).findById(categoryId);
        verify(categoryRepo, never()).save(any(Category.class));
    }

    @Test
    void getAllCategories_shouldReturnListOfCategories() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category 1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");

        Page<Category> page = new PageImpl<>(List.of(category1, category2));
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(categoryRepo.findAll(pageRequest)).thenReturn(page);

        List<CategoryDto> result = categoryService.getAllCategories(0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).getName());
        assertEquals("Category 2", result.get(1).getName());
        verify(categoryRepo, times(1)).findAll(pageRequest);
    }

    @Test
    void getCategory_whenCategoryExists_shouldReturnCategoryDto() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Test Category");

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(category));

        CategoryDto result = categoryService.getCategory(categoryId);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Test Category", result.getName());
        verify(categoryRepo, times(1)).findById(categoryId);
    }

    @Test
    void getCategory_whenCategoryNotFound_shouldThrowNotFoundException() {
        Long categoryId = 1L;

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getCategory(categoryId));
        verify(categoryRepo, times(1)).findById(categoryId);
    }
}
