package ru.practicum.ewm.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.user.UserService;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public UserDetails loadUserByUsername(String username) {
        return userService.getEntityByEmail(username);
    }
}
