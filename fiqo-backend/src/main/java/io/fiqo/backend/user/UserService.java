package io.fiqo.backend.user;

import io.fiqo.backend.exception.ItemNotFoundException;
import io.fiqo.backend.user.dto.ProfileForm;
import io.fiqo.backend.user.dto.UserInfo;
import io.fiqo.backend.user.role.Role;
import io.fiqo.backend.user.role.RoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private static final long MAX_FILE_SIZE = 21474836480L;

  private final @NotNull UserRepository userRepository;
  private final @NotNull UserStorageService userStorageService;
  private final @NotNull RoleRepository roleRepository;
  private final @NotNull UserConverter userConverter;

  public @NotNull UserInfo getUserInfo(final @NotNull UUID userUuid) {
    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    return this.userConverter.toUserInfo(user);
  }

  public void updateProfile(final @NotNull ProfileForm profileForm, final @NotNull UUID userUuid) {
    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    user.setUsername(profileForm.username());
    user.setEmail(profileForm.email());
    user.setFirstName(profileForm.firstName());
    user.setLastName(profileForm.lastName());
    user.setPhone(profileForm.phone());
    user.setBio(profileForm.bio());
    user.setFacebookUrl(profileForm.facebookUrl());
    user.setXUrl(profileForm.xUrl());
    user.setLinkedinUrl(profileForm.linkedinUrl());
    user.setInstagramUrl(profileForm.instagramUrl());

    this.userRepository.save(user);
  }

  public void uploadAvatar(final @NotNull HttpServletRequest request, final @NotNull UUID userUuid)
      throws Exception {
    final Part filePart = request.getPart("avatar");
    this.userStorageService.uploadAvatar(filePart.getInputStream(), userUuid);
  }

  public @NotNull User createUser(
      final @NotNull String username,
      final @NotNull String email,
      final @Nullable String password) {
    final User user =
        User.builder()
            .uuid(UUID.randomUUID())
            .username(username)
            .email(email)
            .password(password)
            .maxFileSize(MAX_FILE_SIZE)
            .build();

    this.setRoles(user);
    return this.userRepository.save(user);
  }

  private void setRoles(final @NotNull User user) {
    final Role role =
        this.roleRepository
            .findByName("USER")
            .orElseThrow(() -> new ItemNotFoundException("roleNotFound"));

    user.setRoles(new HashSet<>(Set.of(role)));
  }
}
