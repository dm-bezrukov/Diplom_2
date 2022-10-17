package ru.practicum.diplom_2;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import ru.practicum.diplom_2.pojos.BasicResponse;
import ru.practicum.diplom_2.pojos.SuccessSignInSignUpResponse;
import ru.practicum.diplom_2.pojos.UserRequest;
import ru.practicum.diplom_2.steps.UsersSteps;
import ru.practicum.diplom_2.utils.UsersUtils;

public class CreateUserTest extends BaseTest {

    @Test
    @DisplayName("Успешная регистрация уникального пользователя")
    public void registerUniqueUserSuccess() {
        UserRequest body = UsersUtils.getUniqueUser();
        SuccessSignInSignUpResponse response = UsersSteps.createUniqueUser(body)
                .then()
                .statusCode(200)
                .extract()
                .as(SuccessSignInSignUpResponse.class);

        Assert.assertTrue("Запрос должен быть выполнен успешно", response.isSuccess());
        Assert.assertFalse("Неверное значения поля refreshToken", response.getRefreshToken().isBlank());
        Assert.assertFalse("Неверное значения поля accessToken", response.getAccessToken().isBlank());
        Assert.assertEquals("Неверное значения поля email" + body.getEmail().toLowerCase(),
                body.getEmail().toLowerCase(), response.getUser().getEmail().toLowerCase());
        Assert.assertEquals("Неверное значения поля name",
                body.getName(), response.getUser().getName());

        UsersSteps.deleteUser(response.getAccessToken())
                .then()
                .statusCode(202);
    }

    @Test
    @DisplayName("При создании дубликата пользователя возвращается 403 ошибка")
    public void registerNotUniqueUserReturns403ErrorMessage() {
        Response baseResponse = UsersSteps.createDuplicateUser();

        if (baseResponse.getStatusCode() == 200) {
            UsersSteps.deleteUser(baseResponse
                    .then()
                    .extract()
                    .as(SuccessSignInSignUpResponse.class)
                    .getAccessToken());
        }

        BasicResponse response = baseResponse
                .then()
                .statusCode(403)
                .extract()
                .as(BasicResponse.class);


        Assert.assertFalse("Запрос не должен быть выполнен успешно", response.isSuccess());
        Assert.assertEquals("Неверный текст ошибки", "User already exists", response.getMessage());
    }

    @Test
    @DisplayName("При создании пользователя без пароля возвращается 403 ошибка")
    public void registerUserWithoutPasswordReturns403ErrorMessage() {
        Response baseResponse = UsersSteps.createUserWithoutPassword();

        if (baseResponse.getStatusCode() == 200) {
            UsersSteps.deleteUser(baseResponse
                    .then()
                    .extract()
                    .as(SuccessSignInSignUpResponse.class)
                    .getAccessToken());
        }

        BasicResponse response = baseResponse
                .then()
                .statusCode(403)
                .extract()
                .as(BasicResponse.class);

        Assert.assertFalse("Запрос не должен быть выполнен успешно", response.isSuccess());
        Assert.assertEquals("Неверный текст ошибки",
                "Email, password and name are required fields", response.getMessage());
    }
}