package com.attendance.system.controller

import com.attendance.system.entity.AttendanceRecord
import com.attendance.system.repository.AttendanceRecordRepository
import com.attendance.system.repository.EmployeeRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminAttendanceControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val employeeRepository: EmployeeRepository,
    @Autowired val attendanceRecordRepository: AttendanceRecordRepository,
) {
    @Test
    @WithMockUser(username = "admin@company.com", roles = ["ADMIN"])
    fun `admin can view an employee's monthly summary`() {
        val employee = requireNotNull(employeeRepository.findByEmail("employee1@company.com"))
        attendanceRecordRepository.save(
            AttendanceRecord(
                employee = employee,
                checkInTime = Instant.parse("2026-07-01T09:00:00Z"),
                checkOutTime = Instant.parse("2026-07-01T17:00:00Z"),
            ),
        )

        mockMvc
            .get("/api/admin/attendance/summary/${employee.id}?year=2026&month=7")
            .andExpect {
                status { isOk() }
                jsonPath("$.daysPresent") { value(1) }
                jsonPath("$.totalHoursWorked") { value(8.0) }
            }
    }

    @Test
    @WithMockUser(username = "employee1@company.com", roles = ["EMPLOYEE"])
    fun `employee cannot view monthly summaries`() {
        val employee = requireNotNull(employeeRepository.findByEmail("employee1@company.com"))

        mockMvc
            .get("/api/admin/attendance/summary/${employee.id}?year=2026&month=7")
            .andExpect { status { isForbidden() } }
    }
}
