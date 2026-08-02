package com.attendance.system.controller

import com.attendance.system.dto.response.MonthlySummaryResponse
import com.attendance.system.exception.EmployeeNotFoundException
import com.attendance.system.repository.EmployeeRepository
import com.attendance.system.service.AttendanceService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/attendance")
class AdminAttendanceController(
    private val attendanceService: AttendanceService,
    private val employeeRepository: EmployeeRepository,
) {
    @GetMapping("/summary/{employeeId}")
    fun monthlySummary(
        @PathVariable employeeId: Long,
        @RequestParam year: Int,
        @RequestParam month: Int,
    ): MonthlySummaryResponse {
        val employee =
            employeeRepository.findById(employeeId).orElseThrow { EmployeeNotFoundException(employeeId.toString()) }
        return attendanceService.monthlySummary(employee, year, month)
    }
}
