package com.attendance.system.config

import com.attendance.system.entity.Employee
import com.attendance.system.repository.EmployeeRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

/**
 * Mirrors [DemoUsers] into the Employee table so the seeded Basic-auth accounts
 * resolve to a real employee record. Guarded by findByEmail so re-running on an
 * already-seeded Postgres database is a no-op.
 */
@Component
class DataSeeder(
    private val employeeRepository: EmployeeRepository,
) : CommandLineRunner {
    override fun run(vararg args: String) {
        DemoUsers.ALL.forEach { demoUser ->
            if (employeeRepository.findByEmail(demoUser.email) == null) {
                employeeRepository.save(
                    Employee(fullName = demoUser.fullName, email = demoUser.email, role = demoUser.role),
                )
            }
        }
    }
}
