package com.example.habittracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI habitTrackerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Habit Tracker API")
                        .description("REST API for managing users, habits, goals, categories and habit logs.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Habit Tracker Team")
                                .email("kirill.ugrumov22@gmail.com"))
                        .license(new License()
                                .name("Educational Use")));
    }
}
