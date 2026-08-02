package com.attendance.system.repository

import com.attendance.system.entity.AttendanceRecord
import com.attendance.system.entity.Employee
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface AttendanceRecordRepository : JpaRepository<AttendanceRecord, Long> {
    fun findFirstByEmployeeAndCheckOutTimeIsNullOrderByCheckInTimeDesc(employee: Employee): AttendanceRecord?

    fun existsByEmployeeAndCheckOutTimeIsNull(employee: Employee): Boolean

    fun findByEmployeeAndCheckInTimeBetweenOrderByCheckInTime(
        employee: Employee,
        from: Instant,
        to: Instant,
    ): List<AttendanceRecord>
}
