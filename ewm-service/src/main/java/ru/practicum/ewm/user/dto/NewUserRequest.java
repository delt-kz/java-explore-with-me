package ru.practicum.ewm.user.dto;

import lombok.Data;

@Data
public class NewUserRequest {
    private String email;
    private String name;
}
