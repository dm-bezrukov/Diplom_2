package ru.practicum.diplom_2.steps;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import ru.practicum.diplom_2.pojos.GetAllIngredientsResponse;
import ru.practicum.diplom_2.pojos.OrderRequest;
import ru.practicum.diplom_2.pojos.SuccessSignInSignUpResponse;
import ru.practicum.diplom_2.utils.ConfigFileReader;
import ru.practicum.diplom_2.utils.OrderUtils;
import java.util.Arrays;
import java.util.List;
import static io.restassured.RestAssured.given;
public class OrderSteps {
    public static RequestSpecification REQUEST_SPECIFICATION =
            new RequestSpecBuilder()
                    .setBaseUri(new ConfigFileReader().getApplicationUrl())
                    .setBasePath("/orders")
                    .setContentType(ContentType.JSON)
                    .build();

    public static RequestSpecification INGREDIENTS_REQUEST_SPECIFICATION =
            new RequestSpecBuilder()
                    .setBaseUri(new ConfigFileReader().getApplicationUrl())
                    .setBasePath("/ingredients")
                    .setContentType(ContentType.JSON)
                    .build();

    @Step("Авторизуемся и создаём новый заказ c ингредиентами")
    public static Response createOrderWithAuth() {
        String accessToken = UsersSteps.signInWithTestUser()
                .as(SuccessSignInSignUpResponse.class).getAccessToken();

        GetAllIngredientsResponse allIngredientsResponse = getAllIngredients()
                .then()
                .statusCode(200)
                .extract()
                .as(GetAllIngredientsResponse.class);

        OrderRequest orderRequest = new OrderRequest(OrderUtils.getRandomIngredients(allIngredientsResponse));

        return given()
                .spec(REQUEST_SPECIFICATION)
                .header("Authorization", accessToken)
                .body(orderRequest)
                .when()
                .post();
    }

    @Step("Авторизуемся и создаём новый заказ без ингредиентов")
    public static Response createOrderWithoutIngredients() {
        String accessToken = UsersSteps.signInWithTestUser()
                .as(SuccessSignInSignUpResponse.class).getAccessToken();

        return given()
                .spec(REQUEST_SPECIFICATION)
                .header("Authorization", accessToken)
                .body(new OrderRequest())
                .when()
                .post();
    }

    @Step("Создаём заказ без авторизации")
    public static Response createOrderWithoutAuth() {
        GetAllIngredientsResponse allIngredientsResponse = getAllIngredients()
                .as(GetAllIngredientsResponse.class);

        List<String> randomIngredients = OrderUtils.getRandomIngredients(allIngredientsResponse);

        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(new OrderRequest(randomIngredients))
                .when()
                .post();
    }

    @Step("Создаём заказ с неверным хешем ингредиентов")
    public static Response createOrderWithWrongIngredients() {
        List<String> wrongIngredientsList = Arrays.asList(RandomStringUtils.randomAlphanumeric(10),
                RandomStringUtils.randomAlphanumeric(10));

        return given()
                .spec(REQUEST_SPECIFICATION)
                .body(new OrderRequest(wrongIngredientsList))
                .when()
                .post();
    }

    @Step("Получаем заказы конкретного пользователя с авторизацией")
    public static Response getUsersOrdersWithAuth() {
        String accessToken = UsersSteps.signInWithTestUser()
                .as(SuccessSignInSignUpResponse.class).getAccessToken();

        return given()
                .spec(REQUEST_SPECIFICATION)
                .header("Authorization", accessToken)
                .when()
                .get();
    }

    @Step("Получаем заказы конкретного пользователя без авторизации")
    public static Response getUsersOrdersWithoutAuth() {
        return given()
                .spec(REQUEST_SPECIFICATION)
                .when()
                .get();
    }

    @Step("Получаем список всех ингредиентов")
    public static Response getAllIngredients() {
        return given()
                .spec(INGREDIENTS_REQUEST_SPECIFICATION)
                .when()
                .get();
    }
}
