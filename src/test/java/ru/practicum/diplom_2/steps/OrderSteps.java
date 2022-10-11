package ru.practicum.diplom_2.steps;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import ru.practicum.diplom_2.pojos.*;
import ru.practicum.diplom_2.utils.OrderUtils;
import java.util.Arrays;
import java.util.List;
import static io.restassured.RestAssured.given;
import static ru.practicum.diplom_2.constants.BaseConstants.BASE_TEST_URL;
import static ru.practicum.diplom_2.constants.OrderConstants.BASE_INGREDIENTS_URL;
import static ru.practicum.diplom_2.constants.OrderConstants.BASE_ORDERS_URL;

public class OrderSteps {
    public static final RequestSpecification REQUEST_SPECIFICATION =
            new RequestSpecBuilder()
                    .setBaseUri(BASE_TEST_URL)
                    .setBasePath(BASE_ORDERS_URL)
                    .setContentType(ContentType.JSON)
                    .build();

    public static final RequestSpecification INGREDIENTS_REQUEST_SPECIFICATION =
            new RequestSpecBuilder()
                    .setBaseUri(BASE_TEST_URL)
                    .setBasePath(BASE_INGREDIENTS_URL)
                    .setContentType(ContentType.JSON)
                    .build();

    @Step("Создаём новый заказ c ингредиентами и авторизацией")
    public static Response createOrderWithAuth(String accessToken) {
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

    @Step("Создаём новый заказ без ингредиентов с авторизацией")
    public static Response createOrderWithoutIngredients(String accessToken) {
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
    public static Response getUsersOrdersWithAuth(String accessToken) {
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