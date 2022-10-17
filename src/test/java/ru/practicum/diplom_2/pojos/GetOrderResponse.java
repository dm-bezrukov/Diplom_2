package ru.practicum.diplom_2.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor
public class GetOrderResponse {
    private List<GetIngredientResponse> ingredients;
    private String _id;
    private String status;
    private int number;
    private int price;
    private String name;
    private String createdAt;
    private String updatedAt;
}