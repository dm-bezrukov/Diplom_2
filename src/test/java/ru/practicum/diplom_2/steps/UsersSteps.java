package ru.practicum.diplom_2.steps;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.practicum.diplom_2.pojos.UserRequest;
import ru.practicum.diplom_2.pojos.SignInRequest;
import ru.practicum.diplom_2.pojos.SuccessSignInSignUpResponse;
import ru.practicum.diplom_2.utils.ConfigFileReader;
import ru.practicum.diplom_2.utils.UsersUtils;

import static io.restassured.RestAssured.given;

public class UsersSteps {
    public static RequestSpecification REQUEST_SPECIFICATION =
            new RequestSpecBuilder()
                    .setBaseUri(new ConfigFileReader().getApplicationUrl())
                    .setBasePath("/auth")
                    .setContentType(ContentType.JSON)
                    .build();

    @Step("Создаём уникального юзера")
    public static Response createUniqueUser(UserRequest body) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(body)
                .when()
                .post("/register");
    }

    @Step("Создаём дубликат юзера")
    public static Response createDuplicateUser() {
        UserRequest user = UsersUtils.getUniqueUser();
        given()
                .spec(REQUEST_SPECIFICATION)
                .body(user)
                .when()
                .post("/register");

        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(user)
                .when()
                .post("/register");
    }

    @Step("Создаём юзера без пароля")
    public static Response createUserWithoutPassword() {
        UserRequest user = UsersUtils.getUniqueUser();
        user.setPassword(null);

        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(user)
                .when()
                .post("/register");
    }

    @Step("Выполняем авторизацию по email и паролю")
    public static Response signInWithEmailAndPassword(String email, String password) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(new SignInRequest(email, password))
                .when()
                .post("/login");
    }

    @Step("Выполняем авторизацию под тестовым пользователем")
    public static Response signInWithTestUser() {
        ConfigFileReader configFileReader = new ConfigFileReader();
        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(new SignInRequest(configFileReader.getTestUserEmail(),
                        configFileReader.getTestUserPassword()))
                .when()
                .post("/login");
    }

    @Step("Выполняем авторизацию под несуществующими данными")
    public static Response signInWithInvalidData() {
        UserRequest user = UsersUtils.getUniqueUser();

        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(user)
                .when()
                .post("/login");
    }

    @Step("Создаём пользователя, авторизуемся и редактируем информацию о юзере")
    public static Response editUserDataWithAuth(UserRequest updatedUser) {
        UserRequest user = UsersUtils.getUniqueUser();
        createUniqueUser(user);

        SuccessSignInSignUpResponse signInResponse = UsersSteps.signInWithEmailAndPassword(user.getEmail(), user.getPassword()).
                as(SuccessSignInSignUpResponse.class);

        String accessToken = signInResponse.getAccessToken();

        return given()
                .spec(REQUEST_SPECIFICATION)
                .header("Authorization", accessToken)
                .body(updatedUser)
                .when()
                .patch("/user");
    }

    @Step("Создаём пользователя, и редактируем информацию о юзере без авторизации")
    public static Response editUserDataWithoutAuth() {
        given()
                .spec(REQUEST_SPECIFICATION)
                .body(UsersUtils.getUniqueUser())
                .when()
                .post("/register")
                .then()
                .statusCode(200);


        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(UsersUtils.getUniqueUser())
                .when()
                .patch("/user");
    }

    @Step("Удаляем пользователя")
    public static Response deleteUser(String accessToken) {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .header("Authorization", accessToken)
                .when()
                .delete("/user");
    }
}