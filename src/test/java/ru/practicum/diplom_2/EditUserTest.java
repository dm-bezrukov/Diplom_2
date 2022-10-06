package ru.practicum.diplom_2;

import io.restassured.RestAssured;
import org.junit.After;
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
    }

    @After
    public void waitBeforeNextTest() {
        //Ожидаем 2 секунды, чтобы избежать 429
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void editUserDataWithAuthSuccess() {
        UserRequest updatedUser = UsersUtils.getUniqueUser();
        SuccessPatchUserResponse response = UsersSteps.editUserDataWithAuth(updatedUser)
                .then()
                .statusCode(200)
                .extract()
                .as(SuccessPatchUserResponse.class);

        Assert.assertTrue(response.isSuccess());
        Assert.assertEquals(updatedUser.getEmail().toLowerCase(), response.getUser().getEmail().toLowerCase());
        Assert.assertEquals(updatedUser.getName(), response.getUser().getName());

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
    public void editUserDataWithoutAuthReturns401ErrorMessage() {
        BasicResponse response = UsersSteps.editUserDataWithoutAuth()
                .then()
                .statusCode(401)
                .extract()
                .as(BasicResponse.class);

        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("You should be authorised", response.getMessage());
    }
}