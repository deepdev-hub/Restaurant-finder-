package com.jptaxi.application.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.jptaxi.application.dto.UserDto;
import com.jptaxi.application.entity.User;
import com.jptaxi.application.entity.UserRole;
import com.jptaxi.application.repository.UserRepository;
import com.jptaxi.application.service.DtoMapper;
import com.jptaxi.application.service.PasswordResetEmailService;
import com.jptaxi.application.service.PasswordResetTokenService;
import com.jptaxi.application.service.StorageImageType;
import com.jptaxi.application.service.SupabaseStorageService;

class UserControllerUploadTests {

    @Test
    void avatarUploadStoresImageUsingAvatarPrefixAndReturnsUpdatedUser() {
        UserRepository userRepository = mock(UserRepository.class);
        DtoMapper mapper = mock(DtoMapper.class);
        SupabaseStorageService storageService = mock(SupabaseStorageService.class);
        User user = new User();
        user.setId("u1");
        user.setName("Demo User");
        user.setEmail("demo@example.com");
        user.setRole(UserRole.diner);
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "avatar.png",
                "image/png",
                "avatar".getBytes(StandardCharsets.UTF_8)
        );
        String avatarUrl = "https://project.supabase.co/storage/v1/object/public/images/avatars/avatar.png";
        UserDto expectedDto = new UserDto("u1", "Demo User", null, "demo@example.com", null, null, UserRole.diner, avatarUrl);
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(storageService.upload(image, StorageImageType.AVATAR)).thenReturn(avatarUrl);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toUserDto(any(User.class))).thenReturn(expectedDto);
        UserController controller = controller(userRepository, mapper, storageService);

        ResponseEntity<?> response = controller.uploadAvatar("u1", image);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedDto);
        verify(storageService).upload(image, StorageImageType.AVATAR);
    }

    @Test
    void avatarUploadRequiresUserId() {
        UserController controller = controller(mock(UserRepository.class), mock(DtoMapper.class), mock(SupabaseStorageService.class));
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "avatar.png",
                "image/png",
                "avatar".getBytes(StandardCharsets.UTF_8)
        );

        ResponseEntity<?> response = controller.uploadAvatar("", image);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private UserController controller(
            UserRepository userRepository,
            DtoMapper mapper,
            SupabaseStorageService storageService
    ) {
        return new UserController(
                userRepository,
                mapper,
                mock(PasswordResetTokenService.class),
                mock(PasswordResetEmailService.class),
                storageService,
                "http://localhost:5173/reset-password"
        );
    }
}
