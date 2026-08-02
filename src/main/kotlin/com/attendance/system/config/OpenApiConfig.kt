package com.attendance.system.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun attendanceSystemOpenApi(): OpenAPI {
        val basicAuthScheme = SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")
        return OpenAPI()
            .info(
                Info()
                    .title("Attendance System API")
                    .description("Employee check-in/check-out, leave requests, and admin reporting for a single department.")
                    .version("v1"),
            ).components(Components().addSecuritySchemes("basicAuth", basicAuthScheme))
            .addSecurityItem(SecurityRequirement().addList("basicAuth"))
    }
}
