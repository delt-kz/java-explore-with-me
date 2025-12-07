package ru.practicum.ewm.compilation.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.CompilationService;
import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(required = false) Integer from,
                                                @RequestParam(required = false) Integer size) {
        return compilationService.getAllCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilations(@PathVariable Long compId) {
        return compilationService.getCompilation(compId);
    }
}
