package com.mehmetozanguven.zad_demo_case.user;

import com.mehmetozanguven.zad_swagger_api.contract.openapi.api.controller.UserControllerApi;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.CreateUserRequest;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.UserApiResponse;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerApi {
    private final UserService userService;

    @Override
    public UserApiResponse doCreateUser(CreateUserRequest createUserRequest) {
        UserModel userModel = userService.createUser(UserModel.builder().email(createUserRequest.getEmail().getValue()).build());
        UserResponse userResponse = new UserResponse()
                .userId(userModel.getId())
                .email(userModel.getEmail())
                ;
        return new UserApiResponse()
                .isSuccess(true)
                .httpStatusCode(HttpStatus.OK.value())
                .response(userResponse)
                ;
    }
}
