package com.mehmetozanguven.zad_demo_case.user.internal;

import com.mehmetozanguven.zad_demo_case.core.ApiApplyOperationResultLogic;
import com.mehmetozanguven.zad_demo_case.core.BusinessUseCase;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.ApiBaseModel;
import com.mehmetozanguven.zad_demo_case.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

@BusinessUseCase
@RequiredArgsConstructor
public class SearchUserByEmailUseCase implements ApiApplyOperationResultLogic<List<UserModel>, List<UserModel>, List<UserModel>> {
    private static final OperationResult<List<UserModel>> EMPTY_RESULT = OperationResult.<List<UserModel>>builder().addReturnedValue(List.of()).build();

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public OperationResult<List<UserModel>> logicBefore(List<UserModel> userModels) {
        if (CollectionUtils.isEmpty(userModels)) {
            return EMPTY_RESULT;
        }
        boolean hasAnyIdEmpty = userModels.stream().anyMatch(model -> StringUtils.isBlank(model.getEmail()));
        if (hasAnyIdEmpty) {
            return EMPTY_RESULT;
        }
        return OperationResult.<List<UserModel>>builder()
                .addReturnedValue(userModels)
                .build();
    }

    @Override
    public OperationResult<List<UserModel>> executeLogic(List<UserModel> userModels) {
        List<User> inDb = userRepository.findUsersByEmails(userModels.stream().map(UserModel::getEmail).toList());
        List<UserModel> returnedModels = userMapper.userModelsFromEntities(inDb);
        return OperationResult.<List<UserModel>>builder()
                .addReturnedValue(returnedModels)
                .build();
    }

    @Override
    public void afterExecution(OperationResult<List<UserModel>> response) {

    }
}
