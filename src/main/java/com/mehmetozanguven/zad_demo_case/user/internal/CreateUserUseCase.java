package com.mehmetozanguven.zad_demo_case.user.internal;

import com.mehmetozanguven.zad_demo_case.core.ApiApplyOperationResultLogic;
import com.mehmetozanguven.zad_demo_case.core.BusinessUseCase;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import com.mehmetozanguven.zad_demo_case.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@BusinessUseCase
@RequiredArgsConstructor
public class CreateUserUseCase implements ApiApplyOperationResultLogic<UserModel, UserModel, UserModel> {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public OperationResult<UserModel> logicBefore(UserModel userModel) {
        if (StringUtils.isBlank(userModel.getEmail())) {
            return OperationResult.<UserModel>builder()
                    .addException(ApiErrorInfo.INVALID_REQUEST)
                    .build();
        }
        return OperationResult.<UserModel>builder()
                .addReturnedValue(userModel)
                .build();
    }

    @Override
    public OperationResult<UserModel> executeLogic(UserModel userModel) {
        User user = userRepository.save(User.builder().email(userModel.getEmail()).build());
        UserModel createdUser = userMapper.userModelFromEntity(user);
        return OperationResult.<UserModel>builder()
                .addReturnedValue(createdUser)
                .build();
    }

    @Override
    public void afterExecution(OperationResult<UserModel> response) {

    }
}
