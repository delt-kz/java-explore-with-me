package ru.practicum.ewm.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.user.User;

@Component
public class CurrentUser {
    public String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Long getUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
