package ru.practicum.ewm.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserService userService;

    private User user;
    private NewUserRequest newUserRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setRole(Role.USER);

        newUserRequest = new NewUserRequest();
        newUserRequest.setName("Test User");
        newUserRequest.setEmail("test@test.com");
    }

    @Test
    void getUsers_whenIdsProvided_shouldReturnFilteredUsers() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepo.findAllByIdIn(List.of(1L), pageRequest)).thenReturn(page);

        List<UserDto> result = userService.getUsers(List.of(1L), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());
        verify(userRepo).findAllByIdIn(List.of(1L), pageRequest);
    }

    @Test
    void getUsers_whenIdsNull_shouldReturnAllUsers() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepo.findAll(pageRequest)).thenReturn(page);

        List<UserDto> result = userService.getUsers(null, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepo).findAll(pageRequest);
    }

    @Test
    void getUsers_whenIdsEmpty_shouldReturnEmptyList() {
        List<UserDto> result = userService.getUsers(List.of(), 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepo, never()).findAllByIdIn(any(), any());
    }

    @Test
    void createUser_shouldSaveAndReturnUser() {
        when(userRepo.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(newUserRequest);

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void deleteUser_shouldCallRepository() {
        userService.deleteUser(1L);
        verify(userRepo).deleteById(1L);
    }

    @Test
    void getEntityByEmail_whenExists_shouldReturnUser() {
        when(userRepo.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        User result = userService.getEntityByEmail("test@test.com");

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void getEntityByEmail_whenNotFound_shouldThrowException() {
        when(userRepo.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getEntityByEmail("notfound@test.com"));
    }
}
