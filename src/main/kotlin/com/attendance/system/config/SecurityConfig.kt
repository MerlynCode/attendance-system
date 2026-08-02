package com.attendance.system.config

import com.attendance.system.entity.Role
import com.attendance.system.repository.EmployeeRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

/**
 * Demo-scale auth: HTTP Basic against an in-memory user store, seeded in [DataSeeder]
 * to mirror the Employee rows by email. A real deployment would swap this for a proper
 * identity provider / JWT flow behind TLS - see README caveats.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(
        employeeRepository: EmployeeRepository,
        passwordEncoder: PasswordEncoder,
    ): UserDetailsService {
        val manager = InMemoryUserDetailsManager()
        DemoUsers.ALL.forEach { demoUser ->
            manager.createUser(
                User
                    .builder()
                    .username(demoUser.email)
                    .password(passwordEncoder.encode(demoUser.password))
                    .roles(demoUser.role.name)
                    .build(),
            )
        }
        return manager
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            headers { frameOptions { disable() } }
            authorizeHttpRequests {
                authorize("/swagger-ui.html", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize("/h2-console/**", permitAll)
                authorize("/api/admin/**", hasRole(Role.ADMIN.name))
                authorize("/api/**", authenticated)
                authorize(anyRequest, denyAll)
            }
            httpBasic { }
        }
        return http.build()
    }
}

data class DemoUser(
    val email: String,
    val password: String,
    val fullName: String,
    val role: Role,
)

object DemoUsers {
    val ALL =
        listOf(
            DemoUser("admin@company.com", "admin123", "Ada Admin", Role.ADMIN),
            DemoUser("employee1@company.com", "employee123", "Grace Employee", Role.EMPLOYEE),
            DemoUser("employee2@company.com", "employee123", "Alan Employee", Role.EMPLOYEE),
        )
}
