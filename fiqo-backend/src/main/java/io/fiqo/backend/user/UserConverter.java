package io.fiqo.backend.user;

import io.fiqo.backend.user.dto.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserConverter {
  @Mapping(
      target = "roles",
      expression = "java(user.getRoles().stream().map(role -> role.getName()).toList())")
  UserInfo toUserInfo(User user);
}
