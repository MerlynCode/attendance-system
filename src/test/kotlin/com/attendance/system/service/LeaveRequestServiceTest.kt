package com.attendance.system.service

import com.attendance.system.entity.Employee
import com.attendance.system.entity.LeaveRequest
import com.attendance.system.entity.LeaveStatus
import com.attendance.system.entity.Role
import com.attendance.system.exception.InvalidLeaveDateRangeException
import com.attendance.system.exception.LeaveRequestNotFoundException
import com.attendance.system.exception.OverlappingLeaveRequestException
import com.attendance.system.repository.LeaveRequestRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.Optional

class LeaveRequestServiceTest {
    private val leaveRequestRepository = mockk<LeaveRequestRepository>()
    private val leaveRequestService = LeaveRequestService(leaveRequestRepository)
    private val employee =
        Employee(fullName = "Ada Lovelace", email = "ada@company.com", role = Role.EMPLOYEE).apply { id = 1L }

    @Test
    fun `submit rejects an end date before the start date`() {
        assertThatThrownBy {
            leaveRequestService.submit(employee, LocalDate.of(2026, 6, 10), LocalDate.of(2026, 6, 5), null)
        }.isInstanceOf(InvalidLeaveDateRangeException::class.java)
    }

    @Test
    fun `submit rejects a request overlapping an existing pending request`() {
        val existing =
            LeaveRequest(
                employee = employee,
                startDate = LocalDate.of(2026, 6, 5),
                endDate = LocalDate.of(2026, 6, 10),
                status = LeaveStatus.PENDING,
            )
        every {
            leaveRequestRepository.findByEmployeeAndStatusIn(employee, listOf(LeaveStatus.PENDING, LeaveStatus.APPROVED))
        } returns listOf(existing)

        assertThatThrownBy {
            leaveRequestService.submit(employee, LocalDate.of(2026, 6, 8), LocalDate.of(2026, 6, 12), null)
        }.isInstanceOf(OverlappingLeaveRequestException::class.java)
    }

    @Test
    fun `submit allows a request that starts right after an existing one ends`() {
        val existing =
            LeaveRequest(
                employee = employee,
                startDate = LocalDate.of(2026, 6, 5),
                endDate = LocalDate.of(2026, 6, 10),
                status = LeaveStatus.PENDING,
            )
        every {
            leaveRequestRepository.findByEmployeeAndStatusIn(employee, listOf(LeaveStatus.PENDING, LeaveStatus.APPROVED))
        } returns listOf(existing)
        val saved = slot<LeaveRequest>()
        every { leaveRequestRepository.save(capture(saved)) } answers { saved.captured.apply { id = 2L } }

        val result = leaveRequestService.submit(employee, LocalDate.of(2026, 6, 11), LocalDate.of(2026, 6, 15), null)

        assertThat(result.status).isEqualTo(LeaveStatus.PENDING)
    }

    @Test
    fun `approve marks a leave request as approved`() {
        val leaveRequest =
            LeaveRequest(employee = employee, startDate = LocalDate.now(), endDate = LocalDate.now())
                .apply { id = 1L }
        every { leaveRequestRepository.findById(1L) } returns Optional.of(leaveRequest)
        every { leaveRequestRepository.save(leaveRequest) } returns leaveRequest

        val result = leaveRequestService.approve(1L)

        assertThat(result.status).isEqualTo(LeaveStatus.APPROVED)
    }

    @Test
    fun `approve throws when the leave request does not exist`() {
        every { leaveRequestRepository.findById(99L) } returns Optional.empty()

        assertThatThrownBy { leaveRequestService.approve(99L) }
            .isInstanceOf(LeaveRequestNotFoundException::class.java)
    }
}
