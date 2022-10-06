package ru.practicum.diplom_2.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor
public class GetUserOrdersResponse {
    private boolean success;
    private List<NestedOrderInfo> orders;
    private int total;
    private int totalToday;
}
