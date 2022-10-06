package ru.practicum.diplom_2.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class NestedUserInfo {
    private String email;
    private String name;
}
