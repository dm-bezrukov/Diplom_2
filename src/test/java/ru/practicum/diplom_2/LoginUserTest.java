package ru.practicum.diplom_2;

import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.diplom_2.pojos.BasicResponse;
import ru.practicum.diplom_2.pojos.SuccessSignInSignUpResponse;
import ru.practicum.diplom_2.steps.UsersSteps;
import ru.practicum.diplom_2.utils.ConfigFileReader;

public class LoginUserTest {
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
    public void signInWithValidDataSuccess() {
        ConfigFileReader configFileReader = new ConfigFileReader();

        SuccessSignInSignUpResponse response = UsersSteps.signInWithTestUser()
                .then()
                .statusCode(200)
                .extract()
                .as(SuccessSignInSignUpResponse.class);

        Assert.assertTrue(response.isSuccess());
        Assert.assertFalse(response.getAccessToken().isBlank());
        Assert.assertFalse(response.getRefreshToken().isBlank());
        Assert.assertEquals(configFileReader.getTestUserName(), response.getUser().getName());
        Assert.assertEquals(configFileReader.getTestUserEmail().toLowerCase(), response.getUser().getEmail().toLowerCase());
    }

    @Test
    public void signInWithInvalidDataReturns401ErrorMessage() {
        BasicResponse response = UsersSteps.signInWithInvalidData()
                .then()
                .statusCode(401)
                .extract()
                .as(BasicResponse.class);

        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("email or password are incorrect", response.getMessage());
    }
}
