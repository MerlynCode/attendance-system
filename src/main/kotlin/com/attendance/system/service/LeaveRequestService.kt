package com.attendance.system.service

import com.attendance.system.dto.response.LeaveRequestResponse
import com.attendance.system.entity.Employee
import com.attendance.system.entity.LeaveRequest
import com.attendance.system.entity.LeaveStatus
import com.attendance.system.exception.InvalidLeaveDateRangeException
import com.attendance.system.exception.LeaveRequestNotFoundException
import com.attendance.system.exception.OverlappingLeaveRequestException
import com.attendance.system.repository.LeaveRequestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * Maps entity -> DTO before returning, while the lazy `employee` association is still
 * accessible: LeaveRequest.employee is FetchType.LAZY, and open-in-view is disabled, so
 * touching it after the transaction closes (e.g. back in the controller) throws
 * LazyInitializationException.
 */
@Service
class LeaveRequestService(
    private val leaveRequestRepository: LeaveRequestRepository,
) {
    @Transactional
    fun submit(
        employee: Employee,
        startDate: LocalDate,
        endDate: LocalDate,
        reason: String?,
    ): LeaveRequestResponse {
        if (endDate.isBefore(startDate)) {
            throw InvalidLeaveDateRangeException()
        }

        val activeRequests =
            leaveRequestRepository.findByEmployeeAndStatusIn(employee, listOf(LeaveStatus.PENDING, LeaveStatus.APPROVED))
        val overlaps = activeRequests.any { !it.endDate.isBefore(startDate) && !it.startDate.isAfter(endDate) }
        if (overlaps) {
            throw OverlappingLeaveRequestException(employee.email)
        }

        val saved =
            leaveRequestRepository.save(
                LeaveRequest(employee = employee, startDate = startDate, endDate = endDate, reason = reason),
            )
        return LeaveRequestResponse.from(saved)
    }

    @Transactional
    fun approve(id: Long): LeaveRequestResponse = updateStatus(id, LeaveStatus.APPROVED)

    @Transactional
    fun reject(id: Long): LeaveRequestResponse = updateStatus(id, LeaveStatus.REJECTED)

    @Transactional(readOnly = true)
    fun listByStatus(status: LeaveStatus?): List<LeaveRequestResponse> {
        val requests = if (status != null) leaveRequestRepository.findByStatus(status) else leaveRequestRepository.findAll()
        return requests.map(LeaveRequestResponse::from)
    }

    private fun updateStatus(
        id: Long,
        status: LeaveStatus,
    ): LeaveRequestResponse {
        val leaveRequest = leaveRequestRepository.findById(id).orElseThrow { LeaveRequestNotFoundException(id) }
        leaveRequest.status = status
        return LeaveRequestResponse.from(leaveRequestRepository.save(leaveRequest))
    }
}
