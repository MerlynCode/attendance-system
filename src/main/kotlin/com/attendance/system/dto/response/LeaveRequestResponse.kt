package com.attendance.system.dto.response

import com.attendance.system.entity.LeaveRequest
import com.attendance.system.entity.LeaveStatus
import java.time.Instant
import java.time.LocalDate

data class LeaveRequestResponse(
    val id: Long,
    val employeeId: Long,
    val employeeName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val reason: String?,
    val status: LeaveStatus,
    val createdAt: Instant,
) {
    companion object {
        fun from(leaveRequest: LeaveRequest) =
            LeaveRequestResponse(
                id = requireNotNull(leaveRequest.id),
                employeeId = requireNotNull(leaveRequest.employee.id),
                employeeName = leaveRequest.employee.fullName,
                startDate = leaveRequest.startDate,
                endDate = leaveRequest.endDate,
                reason = leaveRequest.reason,
                status = leaveRequest.status,
                createdAt = leaveRequest.createdAt,
            )
    }
}
