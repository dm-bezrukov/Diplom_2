package ru.practicum.diplom_2.steps;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.practicum.diplom_2.pojos.UserRequest;
import ru.practicum.diplom_2.pojos.SignInRequest;
import ru.practicum.diplom_2.utils.UsersUtils;
import static io.restassured.RestAssured.given;
import static ru.practicum.diplom_2.constants.BaseConstants.BASE_TEST_URL;
import static ru.practicum.diplom_2.constants.UserConstants.*;

public class UsersSteps {
    public static final RequestSpecification REQUEST_SPECIFICATION =
            new RequestSpecBuilder()
                    .setBaseUri(BASE_TEST_URL)
                    .setBasePath(BASE_AUTH_URL)
                    .setContentType(ContentType.JSON)
                    .build();

    @Step("Создаём уникального юзера")
    public static Response createUniqueUser(UserRequest body) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(body)
                .when()
                .post(BASE_REGISTER_URL);
    }

    @Step("Создаём дубликат юзера")
    public static Response createDuplicateUser() {
        UserRequest user = UsersUtils.getUniqueUser();
        given()
                .spec(REQUEST_SPECIFICATION)
                .body(user)
                .when()
                .post(BASE_REGISTER_URL);

        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(user)
                .when()
                .post(BASE_REGISTER_URL);
    }

    @Step("Создаём юзера без пароля")
    public static Response createUserWithoutPassword() {
        UserRequest user = UsersUtils.getUniqueUser();
        user.setPassword(null);

        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(user)
                .when()
                .post(BASE_REGISTER_URL);
    }

    @Step("Выполняем авторизацию с помощью тела запроса на авторизацию")
    public static Response signInWithSignInRequest(SignInRequest signInRequest) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(signInRequest)
                .when()
                .post(BASE_LOGIN_URL);
    }

    @Step("Выполняем авторизацию под несуществующими данными")
    public static Response signInWithInvalidData() {
        UserRequest user = UsersUtils.getUniqueUser();

        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(user)
                .when()
                .post(BASE_LOGIN_URL);
    }

    @Step("Авторизуемся и редактируем информацию о юзере")
    public static Response editUserDataWithAuth(String accessToken, UserRequest updatedUser) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .header("Authorization", accessToken)
                .body(updatedUser)
                .when()
                .patch(BASE_USER_URL);
    }

    @Step("Редактируем информацию о юзере без авторизации")
    public static Response editUserDataWithoutAuth() {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(UsersUtils.getUniqueUser())
                .when()
                .patch(BASE_USER_URL);
    }

    @Step("Удаляем пользователя")
    public static Response deleteUser(String accessToken) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .header("Authorization", accessToken)
                .when()
                .delete(BASE_USER_URL);
    }
}