package com.attendance.system.repository

import com.attendance.system.entity.Employee
import com.attendance.system.entity.LeaveRequest
import com.attendance.system.entity.LeaveStatus
import org.springframework.data.jpa.repository.JpaRepository

interface LeaveRequestRepository : JpaRepository<LeaveRequest, Long> {
    fun findByEmployeeAndStatusIn(
        employee: Employee,
        statuses: List<LeaveStatus>,
    ): List<LeaveRequest>

    fun findByStatus(status: LeaveStatus): List<LeaveRequest>
}
