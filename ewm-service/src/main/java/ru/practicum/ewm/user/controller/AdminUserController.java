package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Integer> ids,
                                  @RequestParam(required = false) Integer from,
                                  @RequestParam(required = false) Integer size) {

    }

    @PostMapping
    public UserDto createUser(@RequestBody NewUserRequest dto) {

    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {

    }
}
