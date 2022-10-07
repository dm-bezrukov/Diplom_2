package ru.practicum.diplom_2;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.diplom_2.pojos.BasicResponse;
import ru.practicum.diplom_2.pojos.SuccessPatchUserResponse;
import ru.practicum.diplom_2.pojos.SuccessSignInSignUpResponse;
import ru.practicum.diplom_2.pojos.UserRequest;
import ru.practicum.diplom_2.steps.UsersSteps;
import ru.practicum.diplom_2.utils.UsersUtils;

public class EditUserTest {
    @Before
    public void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        //Ожидаем 3 секунды, чтобы избежать 429
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Успешное редактирование данных пользователя с авторизацией")
    public void editUserDataWithAuthSuccess() {
        UserRequest updatedUser = UsersUtils.getUniqueUser();
        SuccessPatchUserResponse response = UsersSteps.editUserDataWithAuth(updatedUser)
                .then()
                .statusCode(200)
                .extract()
                .as(SuccessPatchUserResponse.class);

        Assert.assertTrue("Запрос должен быть выполнен успешно", response.isSuccess());
        Assert.assertEquals("Неверное значение поля email", updatedUser.getEmail().toLowerCase(),
                response.getUser().getEmail().toLowerCase());
        Assert.assertEquals("Неверное значение поля name", updatedUser.getName(), response.getUser().getName());

        String accessToken = UsersSteps.signInWithEmailAndPassword(updatedUser.getEmail(), updatedUser.getPassword())
                .then()
                .extract()
                .as(SuccessSignInSignUpResponse.class)
                .getAccessToken();

        UsersSteps.deleteUser(accessToken)
                .then()
                .statusCode(202);
    }

    @Test
    @DisplayName("Редактирование данных пользователя без авторизации возвращает ошибку")
    public void editUserDataWithoutAuthReturns401ErrorMessage() {
        BasicResponse response = UsersSteps.editUserDataWithoutAuth()
                .then()
                .statusCode(401)
                .extract()
                .as(BasicResponse.class);

        Assert.assertFalse("Запрос не должен быть выполнен успешно", response.isSuccess());
        Assert.assertEquals("Неверный текст ошибки", "You should be authorised", response.getMessage());
    }
}