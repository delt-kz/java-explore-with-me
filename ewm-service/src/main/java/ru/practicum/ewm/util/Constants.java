package ru.practicum.ewm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

}
