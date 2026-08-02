package com.attendance.system.dto.response

data class MonthlySummaryResponse(
    val employeeId: Long,
    val employeeName: String,
    val year: Int,
    val month: Int,
    val daysPresent: Int,
    val totalHoursWorked: Double,
    val records: List<AttendanceRecordResponse>,
)
