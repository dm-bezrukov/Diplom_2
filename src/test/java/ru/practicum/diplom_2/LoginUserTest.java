package ru.practicum.diplom_2;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Assert;
import org.junit.Test;
import ru.practicum.diplom_2.pojos.BasicResponse;
import ru.practicum.diplom_2.pojos.SignInRequest;
import ru.practicum.diplom_2.pojos.SuccessSignInSignUpResponse;
import ru.practicum.diplom_2.steps.UsersSteps;

public class LoginUserTest extends BaseTest {
    @Test
    @DisplayName("Успешная авторизация с верными кредами")
    public void signInWithValidDataSuccess() {
        SuccessSignInSignUpResponse response = UsersSteps.signInWithSignInRequest(new SignInRequest(user.getEmail(),
                        user.getPassword()))
                .then()
                .statusCode(200)
                .extract()
                .as(SuccessSignInSignUpResponse.class);

        Assert.assertTrue("Запрос должен быть выполнен успешно", response.isSuccess());
        Assert.assertFalse("Неверное значения поля accessToken", response.getAccessToken().isBlank());
        Assert.assertFalse("Неверное значения поля refreshToken", response.getRefreshToken().isBlank());
        Assert.assertEquals("Неверное значения поля name", user.getName(),
                response.getUser().getName());
        Assert.assertEquals("Неверное значения поля email",
                user.getEmail().toLowerCase(), response.getUser().getEmail().toLowerCase());
    }

    @Test
    @DisplayName("Авторизация с неверными кредами возвращает 401")
    public void signInWithInvalidDataReturns401ErrorMessage() {
        BasicResponse response = UsersSteps.signInWithInvalidData()
                .then()
                .statusCode(401)
                .extract()
                .as(BasicResponse.class);

        Assert.assertFalse("Запрос не должен быть выполнен успешно", response.isSuccess());
        Assert.assertEquals("Неверный текст ошибки", "email or password are incorrect",
                response.getMessage());
    }
}