package ru.practicum.diplom_2.utils;

import ru.practicum.diplom_2.pojos.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderUtils {
    public static List<String> getRandomIngredients(GetAllIngredientsResponse allIngredientsResponse) {
        List<GetIngredientResponse> allIngredientsResponseData = allIngredientsResponse.getData();
        List<String> ingredients = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            String id = allIngredientsResponseData.get(random.nextInt(allIngredientsResponseData.size()))
                    .get_id();
            ingredients.add(id);
        }

        return ingredients;
    }
}