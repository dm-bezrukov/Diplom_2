package ru.practicum.diplom_2.constants;

import ru.practicum.diplom_2.utils.ConfigFileReader;

public class BaseConstants {
    public final static String BASE_TEST_URL = new ConfigFileReader().getApplicationUrl();
}
