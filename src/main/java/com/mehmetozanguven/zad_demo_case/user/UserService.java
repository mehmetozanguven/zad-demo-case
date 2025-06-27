package com.mehmetozanguven.zad_demo_case.user;

import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import com.mehmetozanguven.zad_demo_case.core.pipeline.ApiPipeline;
import com.mehmetozanguven.zad_demo_case.user.internal.CreateUserUseCase;
import com.mehmetozanguven.zad_demo_case.user.internal.SearchUserByEmailUseCase;
import com.mehmetozanguven.zad_demo_case.user.internal.SearchUserByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final SearchUserByEmailUseCase searchUserByEmailUseCase;
    private final SearchUserByIdUseCase searchUserByIdUseCase;
    private final CreateUserUseCase createUserUseCase;

    public Optional<UserModel> isUserExistsById(UserModel userId) {
        OperationResult<List<UserModel>> foundModels = searchUserByIdUseCase.applyBusiness(
                List.of(userId)
        );
        if (foundModels.isValid()) {
            return Optional.of(foundModels.getReturnedValue().getFirst());
        } else {
            return Optional.empty();
        }
    }

    public UserModel createUser(UserModel newUser) {
        ApiPipeline<UserModel, UserModel> pipeline = ApiPipeline
                .<UserModel>start()
                .pipe(user -> {
                    OperationResult<List<UserModel>> foundModels = searchUserByEmailUseCase.applyBusiness(
                            List.of(user)
                    );
                    if (foundModels.isValid() && !foundModels.getReturnedValue().isEmpty()) {
                        return OperationResult.<UserModel>builder()
                                .addException(ApiErrorInfo.USER_ALREADY_EXISTS)
                                .build();
                    } else {
                        return OperationResult.<UserModel>builder()
                                .addReturnedValue(user)
                                .build();
                    }
                }).pipe(createUserUseCase::applyBusiness);

        return pipeline.execute(newUser);
    }

}
