package com.attendance.system.controller

import com.attendance.system.dto.response.AttendanceRecordResponse
import com.attendance.system.service.AttendanceService
import com.attendance.system.service.CurrentEmployeeProvider
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/attendance")
class AttendanceController(
    private val attendanceService: AttendanceService,
    private val currentEmployeeProvider: CurrentEmployeeProvider,
) {
    @PostMapping("/check-in")
    fun checkIn(authentication: Authentication): ResponseEntity<AttendanceRecordResponse> {
        val employee = currentEmployeeProvider.resolve(authentication)
        val record = attendanceService.checkIn(employee)
        return ResponseEntity.status(HttpStatus.CREATED).body(AttendanceRecordResponse.from(record))
    }

    @PostMapping("/check-out")
    fun checkOut(authentication: Authentication): ResponseEntity<AttendanceRecordResponse> {
        val employee = currentEmployeeProvider.resolve(authentication)
        val record = attendanceService.checkOut(employee)
        return ResponseEntity.ok(AttendanceRecordResponse.from(record))
    }
}
