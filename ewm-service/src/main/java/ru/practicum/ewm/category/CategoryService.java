package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepo;

    @Transactional
    public CategoryDto createCategory(NewCategoryDto dto) {
        Category category = CategoryMapper.fromNew(dto);
        return CategoryMapper.toDto(categoryRepo.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, NewCategoryDto dto) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        if (dto.getName() != null) {
            category.setName(dto.getName());
        }

        return CategoryMapper.toDto(categoryRepo.save(category));
    }

    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        Page<Category> page = categoryRepo.findAll(pageRequest);
        return CategoryMapper.toDto(page.getContent());
    }

    public CategoryDto getCategory(Long id) {
        return CategoryMapper.toDto(categoryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found")));
    }
}
