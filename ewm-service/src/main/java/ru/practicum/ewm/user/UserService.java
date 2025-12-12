package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        Page<User> users;

        if (ids != null) {
            if (ids.isEmpty()) {
                return List.of();
            }
            users = userRepo.findAllByIdIn(ids, pageRequest);
        } else {
            users = userRepo.findAll(pageRequest);
        }

        return UserMapper.toDto(users.getContent());
    }

    @Transactional
    public UserDto createUser(NewUserRequest dto) {
        User user = UserMapper.fromNew(dto);
        return UserMapper.toDto(userRepo.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
}
