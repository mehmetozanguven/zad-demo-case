package com.mehmetozanguven.zad_demo_case.user.internal;

import com.mehmetozanguven.zad_demo_case.user.UserModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserModel userModelFromEntity(User user);
    List<UserModel> userModelsFromEntities(List<User> users);
}
