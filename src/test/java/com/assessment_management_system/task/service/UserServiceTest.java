package com.assessment_management_system.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.assessment_management_system.task.dto.CreateUserRequest;
import com.assessment_management_system.task.dto.UserResponse;
import com.assessment_management_system.task.entity.User;
import com.assessment_management_system.task.enums.Role;
import com.assessment_management_system.task.exception.DuplicateResourceException;
import com.assessment_management_system.task.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createPersistsEncodedPasswordAndNormalizedEmail() {
        when(userRepository.existsByEmailIgnoreCase("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password1")).thenReturn("encoded-secret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(5L);
            return user;
        });

        UserResponse response = userService.create(new CreateUserRequest(
                " Ada ",
                "Admin@Example.com",
                "password1",
                Role.ADMIN
        ));

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.email()).isEqualTo("admin@example.com");
        assertThat(response.role()).isEqualTo(Role.ADMIN);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded-secret");
        assertThat(captor.getValue().getName()).isEqualTo("Ada");
    }

    @Test
    void createRejectsDuplicateEmail() {
        when(userRepository.existsByEmailIgnoreCase("admin@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(new CreateUserRequest(
                "Ada",
                "admin@example.com",
                "password1",
                Role.ADMIN
        ))).isInstanceOf(DuplicateResourceException.class);
    }
}