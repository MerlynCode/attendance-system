package com.attendance.system.service

import com.attendance.system.entity.Employee
import com.attendance.system.exception.EmployeeNotFoundException
import com.attendance.system.repository.EmployeeRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

/**
 * Employees only ever act on their own record: endpoints derive the Employee from the
 * authenticated principal (username = email) rather than trusting a client-supplied id.
 */
@Component
class CurrentEmployeeProvider(
    private val employeeRepository: EmployeeRepository,
) {
    fun resolve(authentication: Authentication): Employee =
        employeeRepository.findByEmail(authentication.name)
            ?: throw EmployeeNotFoundException(authentication.name)
}
