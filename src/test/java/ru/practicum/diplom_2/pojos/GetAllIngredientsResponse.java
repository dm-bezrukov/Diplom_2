package ru.practicum.diplom_2.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllIngredientsResponse {
    private boolean success;
    private List<GetIngredientResponse> data;
}
