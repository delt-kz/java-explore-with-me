package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50)
    private String title;

    public boolean hasEvents() {
        return events != null;
    }

    public boolean hasPinned() {
        return pinned != null;
    }

    public boolean hasTitle() {
        return title != null;
    }
}
