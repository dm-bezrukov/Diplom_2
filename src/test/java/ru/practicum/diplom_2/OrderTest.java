package ru.practicum.diplom_2;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.diplom_2.pojos.GetOrderResponse;
import ru.practicum.diplom_2.pojos.GetUserOrdersResponse;
import ru.practicum.diplom_2.pojos.BasicResponse;
import ru.practicum.diplom_2.pojos.SuccessCreateOrderResponse;
import ru.practicum.diplom_2.steps.OrderSteps;

public class OrderTest {

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

    @DisplayName("Создание заказа с авторизацией и ингредиентами проходит успешно")
    @Test
    public void createOrderWithAuthSuccess() {
        SuccessCreateOrderResponse getOrderResponse = OrderSteps.createOrderWithAuth()
                .then()
                .statusCode(200)
                .extract()
                .as(SuccessCreateOrderResponse.class);

        GetOrderResponse order = getOrderResponse.getOrder();

        Assert.assertTrue(getOrderResponse.isSuccess());
        Assert.assertFalse(getOrderResponse.getName().isBlank());
        Assert.assertFalse(order.getIngredients().isEmpty());
        Assert.assertFalse(order.get_id().isBlank());
        Assert.assertFalse(order.getStatus().isBlank());
        Assert.assertNotEquals(0, order.getPrice());
        Assert.assertFalse(order.getName().isBlank());
        Assert.assertFalse(order.getCreatedAt().isBlank());
        Assert.assertFalse(order.getUpdatedAt().isBlank());
    }

    @DisplayName("Создание заказа с авторизацией и без ингредиентов не выполняется")
    @Test
    public void createOrderWithoutIngredients400ErrorMessage() {
        BasicResponse response = OrderSteps.createOrderWithoutIngredients()
                .then()
                .statusCode(400)
                .extract()
                .as(BasicResponse.class);

        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("Ingredient ids must be provided", response.getMessage());
    }

    @DisplayName("Создание заказа без авторизации не выполняется")
    @Test
    public void createOrderWithoutAuth401ErrorMessage() {
        BasicResponse response = OrderSteps.createOrderWithoutAuth()
                .then()
                .statusCode(401)
                .extract()
                .as(BasicResponse.class);

        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("You should be authorised", response.getMessage());
    }

    @DisplayName("Создание заказа с неверным хешом ингредиентов не выполняется")
    @Test
    public void createOrderWithWrongIngredientsReturns500ErrorMessage() {
        OrderSteps.createOrderWithWrongIngredients()
                .then()
                .statusCode(500);
    }

    @DisplayName("Получение заказов пользователя с авторизацией проходит успешно")
    @Test
    public void getUserOrderWithAuthSuccess() {
        GetUserOrdersResponse response = OrderSteps.getUsersOrdersWithAuth()
                .then()
                .statusCode(200)
                .extract()
                .as(GetUserOrdersResponse.class);

        Assert.assertTrue(response.isSuccess());
        Assert.assertFalse(response.getOrders().isEmpty());
        Assert.assertNotEquals(0, response.getTotal());
        Assert.assertNotEquals(0, response.getTotalToday());
    }

    @DisplayName("Получение заказов пользователя без авторизации не выполняется")
    @Test
    public void getUserOrderWithoutAuth401ErrorMessage() {
        BasicResponse response = OrderSteps.getUsersOrdersWithoutAuth()
                .then()
                .statusCode(401)
                .extract()
                .as(BasicResponse.class);

        Assert.assertFalse(response.isSuccess());
        Assert.assertEquals("You should be authorised", response.getMessage());
    }
}