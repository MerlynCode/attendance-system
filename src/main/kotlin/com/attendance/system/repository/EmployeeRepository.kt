package com.attendance.system.repository

import com.attendance.system.entity.Employee
import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeRepository : JpaRepository<Employee, Long> {
    fun findByEmail(email: String): Employee?
}
