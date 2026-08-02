package com.attendance.system.repository

import com.attendance.system.entity.Employee
import com.attendance.system.entity.Role
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest

@DataJpaTest
class EmployeeRepositoryTest(
    @Autowired val employeeRepository: EmployeeRepository,
) {
    @Test
    fun `findByEmail returns saved employee`() {
        employeeRepository.save(Employee(fullName = "Ada Lovelace", email = "ada@company.com", role = Role.EMPLOYEE))

        val found = employeeRepository.findByEmail("ada@company.com")

        assertThat(found).isNotNull
        assertThat(found?.fullName).isEqualTo("Ada Lovelace")
        assertThat(found?.role).isEqualTo(Role.EMPLOYEE)
    }

    @Test
    fun `findByEmail returns null when no employee matches`() {
        assertThat(employeeRepository.findByEmail("nobody@company.com")).isNull()
    }
}
