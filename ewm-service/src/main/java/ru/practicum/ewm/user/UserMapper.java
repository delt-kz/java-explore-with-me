package ru.practicum.ewm.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(user.getEmail(), user.getId(), user.getName());
    }

    public static List<UserDto> toDto(List<User> users) {
        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public static User fromNew(NewUserRequest dto) {
        User user =new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }
}
