package ru.practicum.ewm.category;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {

    public static CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static List<CategoryDto> toDto(List<Category> categories) {
        return categories.stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    public static Category fromNew(NewCategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }
}
