package com.mehmetozanguven.zad_demo_case.flow;

import com.mehmetozanguven.zad_demo_case.core.BaseSpringBootTest;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ZadUserAndAccountFlowTest extends BaseSpringBootTest {

    @Test
    void test_zadUserFlow() {
        // create user
        var request = new CreateUserRequest()
                .email(new EmailTextValue().value("test@test.com"));
        var createUserResponseEntity = testRestTemplate.postForEntity(generateUri("/api/zad/user"), request, UserApiResponse.class);
        UserApiResponse newUser = createUserResponseEntity.getBody();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(newUser),
                () -> Assertions.assertTrue(newUser.getIsSuccess()),
                () -> Assertions.assertTrue(newUser.getErrorResponses().isEmpty()),
                () -> Assertions.assertEquals("test@test.com",  newUser.getResponse().getEmail()),
                () -> Assertions.assertNotNull(newUser.getResponse().getUserId())
        );

        // create two accounts for user
        var tryAccountRequest = new CreateAccountRequest().accountType(ApiAccountType.TRY).userId(new AppTextValue(newUser.getResponse().getUserId()));
        var usdAccountRequest = new CreateAccountRequest().accountType(ApiAccountType.USD).userId(new AppTextValue(newUser.getResponse().getUserId()));

        AccountApiResponse tryAccountResponse = testRestTemplate.postForEntity(generateUri("/api/zad/account"), tryAccountRequest, AccountApiResponse.class).getBody();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(tryAccountResponse),
                () -> Assertions.assertTrue(tryAccountResponse.getIsSuccess()),
                () -> Assertions.assertTrue(tryAccountResponse.getErrorResponses().isEmpty()),
                () -> Assertions.assertEquals(1,  tryAccountResponse.getResponse().getAccountIds().size()),
                () -> Assertions.assertEquals(AppCurrencyCode.TRY,  tryAccountResponse.getResponse().getTotalBalance().getCurrencyType())
        );

        AccountApiResponse usdAccountResponse = testRestTemplate.postForEntity(generateUri("/api/zad/account"), usdAccountRequest, AccountApiResponse.class).getBody();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(usdAccountResponse),
                () -> Assertions.assertTrue(usdAccountResponse.getIsSuccess()),
                () -> Assertions.assertTrue(usdAccountResponse.getErrorResponses().isEmpty()),
                () -> Assertions.assertEquals(1,  usdAccountResponse.getResponse().getAccountIds().size()),
                () -> Assertions.assertEquals(AppCurrencyCode.USD,  usdAccountResponse.getResponse().getTotalBalance().getCurrencyType())
        );

        // user can not generate same account type more than once
        AccountApiResponse failedUsdAccountResponse = testRestTemplate.postForEntity(generateUri("/api/zad/account"), usdAccountRequest, AccountApiResponse.class).getBody();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(failedUsdAccountResponse),
                () -> Assertions.assertFalse(failedUsdAccountResponse.getIsSuccess()),
                () -> Assertions.assertEquals(ApiErrorInfo.ACCOUNT_OPERATION_FAILED.httpStatus.value(), failedUsdAccountResponse.getHttpStatusCode()),
                () -> Assertions.assertFalse(failedUsdAccountResponse.getErrorResponses().isEmpty()),
                () -> Assertions.assertEquals(ApiErrorInfo.ACCOUNT_OPERATION_FAILED.code, failedUsdAccountResponse.getErrorResponses().getFirst().getProperties().get("code"))
        );
    }
}
