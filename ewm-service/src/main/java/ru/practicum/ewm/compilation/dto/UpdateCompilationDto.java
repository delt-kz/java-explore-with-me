package ru.practicum.ewm.compilation.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    private String title;

    public boolean hasEvents() {
        return events != null && !events.isEmpty();
    }

    public boolean hasPinned() {
        return pinned != null;
    }

    public boolean hasTitle() {
        return title != null && !title.isBlank();
    }
}
