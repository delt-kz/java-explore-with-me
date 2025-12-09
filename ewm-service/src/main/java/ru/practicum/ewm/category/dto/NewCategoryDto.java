package ru.practicum.ewm.category.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCategoryDto {
    @NotNull
    @Size(min = 1, max = 50)
    private String name;
}
