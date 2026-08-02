package com.attendance.system.controller

import com.attendance.system.dto.request.LeaveRequestSubmission
import com.attendance.system.dto.response.LeaveRequestResponse
import com.attendance.system.service.CurrentEmployeeProvider
import com.attendance.system.service.LeaveRequestService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/leave-requests")
class LeaveRequestController(
    private val leaveRequestService: LeaveRequestService,
    private val currentEmployeeProvider: CurrentEmployeeProvider,
) {
    @PostMapping
    fun submit(
        authentication: Authentication,
        @Valid @RequestBody submission: LeaveRequestSubmission,
    ): ResponseEntity<LeaveRequestResponse> {
        val employee = currentEmployeeProvider.resolve(authentication)
        val leaveRequest =
            leaveRequestService.submit(
                employee = employee,
                startDate = requireNotNull(submission.startDate),
                endDate = requireNotNull(submission.endDate),
                reason = submission.reason,
            )
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveRequest)
    }
}
