package ru.practicum.diplom_2;

import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.diplom_2.pojos.BasicResponse;
import ru.practicum.diplom_2.pojos.SuccessSignInSignUpResponse;
import ru.practicum.diplom_2.pojos.UserRequest;
import ru.practicum.diplom_2.steps.UsersSteps;
import ru.practicum.diplom_2.utils.UsersUtils;

public class CreateUserTest {
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
    public void registerUniqueUserSuccess() {
        UserRequest body = UsersUtils.getUniqueUser();
        SuccessSignInSignUpResponse response = UsersSteps.createUniqueUser(body)
                .then()
                .statusCode(200)
                .extract()
                .as(SuccessSignInSignUpResponse.class);

        Assert.assertTrue(response.isSuccess());
        Assert.assertFalse(response.getRefreshToken().isBlank());
        Assert.assertFalse(response.getAccessToken().isBlank());
        Assert.assertEquals(body.getEmail().toLowerCase(), response.getUser().getEmail().toLowerCase());
        Assert.assertEquals(body.getName(), response.getUser().getName());

        UsersSteps.deleteUser(response.getAccessToken())
                .then()
                .statusCode(202);
    }

    @Test
    public void registerNotUniqueUserReturns403ErrorMessage() {
        BasicResponse response = UsersSteps.createDuplicateUser()
                .then()
                .statusCode(403)
                .extract()
                .as(BasicResponse.class);

        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("User already exists", response.getMessage());
    }

    @Test
    public void registerUserWithoutPasswordReturns403ErrorMessage() {
        BasicResponse response = UsersSteps.createUserWithoutPassword()
                .then()
                .statusCode(403)
                .extract()
                .as(BasicResponse.class);

        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("Email, password and name are required fields", response.getMessage());
    }
}
