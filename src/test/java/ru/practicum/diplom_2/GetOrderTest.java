package ru.practicum.diplom_2;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.practicum.diplom_2.pojos.BasicResponse;
import ru.practicum.diplom_2.pojos.GetUserOrdersResponse;
import ru.practicum.diplom_2.steps.OrderSteps;

public class GetOrderTest {
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

    @DisplayName("Получение заказов пользователя с авторизацией проходит успешно")
    @Test
    public void getUserOrderWithAuthSuccess() {
        GetUserOrdersResponse response = OrderSteps.getUsersOrdersWithAuth()
                .then()
                .statusCode(200)
                .extract()
                .as(GetUserOrdersResponse.class);

        Assert.assertTrue("Запрос должен быть выполнен успешно", response.isSuccess());
        Assert.assertFalse("Список заказов не должен быть пустым", response.getOrders().isEmpty());
        Assert.assertNotEquals("Общее число заказов не должно быть равно 0", 0, response.getTotal());
        Assert.assertNotEquals("Число заказов за сегодня не должно быть равно 0", 0,
                response.getTotalToday());
    }

    @DisplayName("Получение заказов пользователя без авторизации не выполняется")
    @Test
    public void getUserOrderWithoutAuth401ErrorMessage() {
        BasicResponse response = OrderSteps.getUsersOrdersWithoutAuth()
                .then()
                .statusCode(401)
                .extract()
                .as(BasicResponse.class);

        Assert.assertFalse("Запрос не должен быть выполнен успешно", response.isSuccess());
        Assert.assertEquals("Неверный текст ошибки", "You should be authorised", response.getMessage());
    }
}