package com.attendance.system.service

import com.attendance.system.entity.AttendanceRecord
import com.attendance.system.entity.Employee
import com.attendance.system.entity.Role
import com.attendance.system.exception.AlreadyCheckedInException
import com.attendance.system.exception.NoActiveCheckInException
import com.attendance.system.repository.AttendanceRecordRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.Instant

class AttendanceServiceTest {
    private val attendanceRecordRepository = mockk<AttendanceRecordRepository>()
    private val attendanceService = AttendanceService(attendanceRecordRepository)
    private val employee = Employee(fullName = "Ada Lovelace", email = "ada@company.com", role = Role.EMPLOYEE)

    @Test
    fun `checkIn creates a record when no open record exists`() {
        every { attendanceRecordRepository.existsByEmployeeAndCheckOutTimeIsNull(employee) } returns false
        val saved = slot<AttendanceRecord>()
        every { attendanceRecordRepository.save(capture(saved)) } answers { saved.captured }

        val result = attendanceService.checkIn(employee)

        assertThat(result.employee).isEqualTo(employee)
        assertThat(result.checkOutTime).isNull()
        verify { attendanceRecordRepository.save(any()) }
    }

    @Test
    fun `checkIn rejects a second check-in while one is already open`() {
        every { attendanceRecordRepository.existsByEmployeeAndCheckOutTimeIsNull(employee) } returns true

        assertThatThrownBy { attendanceService.checkIn(employee) }
            .isInstanceOf(AlreadyCheckedInException::class.java)
    }

    @Test
    fun `checkOut closes the open record`() {
        val open = AttendanceRecord(employee = employee, checkInTime = Instant.now().minusSeconds(3600))
        every {
            attendanceRecordRepository.findFirstByEmployeeAndCheckOutTimeIsNullOrderByCheckInTimeDesc(employee)
        } returns open
        every { attendanceRecordRepository.save(open) } returns open

        val result = attendanceService.checkOut(employee)

        assertThat(result.checkOutTime).isNotNull()
    }

    @Test
    fun `checkOut rejects when there is no open record`() {
        every {
            attendanceRecordRepository.findFirstByEmployeeAndCheckOutTimeIsNullOrderByCheckInTimeDesc(employee)
        } returns null

        assertThatThrownBy { attendanceService.checkOut(employee) }
            .isInstanceOf(NoActiveCheckInException::class.java)
    }

    @Test
    fun `monthlySummary counts days and hours only from closed records`() {
        employee.id = 1L
        val inMonthClosed =
            AttendanceRecord(
                employee = employee,
                checkInTime = Instant.parse("2026-06-10T09:00:00Z"),
                checkOutTime = Instant.parse("2026-06-10T17:00:00Z"),
            ).apply { id = 1L }
        val stillOpen =
            AttendanceRecord(employee = employee, checkInTime = Instant.parse("2026-06-15T09:00:00Z"))
                .apply { id = 2L }
        every {
            attendanceRecordRepository.findByEmployeeAndCheckInTimeBetweenOrderByCheckInTime(employee, any(), any())
        } returns listOf(inMonthClosed, stillOpen)

        val summary = attendanceService.monthlySummary(employee, 2026, 6)

        assertThat(summary.daysPresent).isEqualTo(1)
        assertThat(summary.totalHoursWorked).isEqualTo(8.0)
        assertThat(summary.records).hasSize(2)
    }
}
