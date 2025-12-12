package ru.practicum.ewm.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.client.StatisticsClient;

@Configuration
public class AppConfig {

    @Bean
    public StatisticsClient statisticsClient(@Value("${statistics.client.url}") String url) {
        return new StatisticsClient(url);
    }
}
