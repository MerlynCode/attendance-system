package com.attendance.system.service

import com.attendance.system.dto.response.AttendanceRecordResponse
import com.attendance.system.dto.response.MonthlySummaryResponse
import com.attendance.system.entity.AttendanceRecord
import com.attendance.system.entity.Employee
import com.attendance.system.exception.AlreadyCheckedInException
import com.attendance.system.exception.NoActiveCheckInException
import com.attendance.system.repository.AttendanceRecordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneOffset

@Service
class AttendanceService(
    private val attendanceRecordRepository: AttendanceRecordRepository,
) {
    @Transactional
    fun checkIn(employee: Employee): AttendanceRecord {
        if (attendanceRecordRepository.existsByEmployeeAndCheckOutTimeIsNull(employee)) {
            throw AlreadyCheckedInException(employee.email)
        }
        return attendanceRecordRepository.save(AttendanceRecord(employee = employee, checkInTime = Instant.now()))
    }

    @Transactional
    fun checkOut(employee: Employee): AttendanceRecord {
        val openRecord =
            attendanceRecordRepository.findFirstByEmployeeAndCheckOutTimeIsNullOrderByCheckInTimeDesc(employee)
                ?: throw NoActiveCheckInException(employee.email)
        openRecord.checkOutTime = Instant.now()
        return attendanceRecordRepository.save(openRecord)
    }

    /**
     * Dates are compared in UTC rather than a per-employee timezone - out of scope for this
     * single-department demo, but would matter for a real multi-timezone workforce.
     */
    fun monthlySummary(
        employee: Employee,
        year: Int,
        month: Int,
    ): MonthlySummaryResponse {
        val yearMonth = YearMonth.of(year, month)
        val from = yearMonth.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant()
        val to =
            yearMonth
                .atEndOfMonth()
                .plusDays(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .minusNanos(1)

        val records =
            attendanceRecordRepository.findByEmployeeAndCheckInTimeBetweenOrderByCheckInTime(employee, from, to)
        val closedRecords = records.filter { it.checkOutTime != null }

        val daysPresent = closedRecords.map { it.checkInTime.atZone(ZoneOffset.UTC).toLocalDate() }.distinct().size
        val totalHoursWorked =
            closedRecords.sumOf { Duration.between(it.checkInTime, it.checkOutTime).toMinutes() / 60.0 }

        return MonthlySummaryResponse(
            employeeId = requireNotNull(employee.id),
            employeeName = employee.fullName,
            year = year,
            month = month,
            daysPresent = daysPresent,
            totalHoursWorked = totalHoursWorked,
            records = records.map(AttendanceRecordResponse::from),
        )
    }
}
