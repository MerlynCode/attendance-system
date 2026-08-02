package com.attendance.system.controller

import com.attendance.system.dto.response.LeaveRequestResponse
import com.attendance.system.entity.LeaveStatus
import com.attendance.system.service.LeaveRequestService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/leave-requests")
class AdminController(
    private val leaveRequestService: LeaveRequestService,
) {
    @GetMapping
    fun list(
        @RequestParam(required = false) status: LeaveStatus?,
    ): List<LeaveRequestResponse> = leaveRequestService.listByStatus(status)

    @PatchMapping("/{id}/approve")
    fun approve(
        @PathVariable id: Long,
    ): LeaveRequestResponse = leaveRequestService.approve(id)

    @PatchMapping("/{id}/reject")
    fun reject(
        @PathVariable id: Long,
    ): LeaveRequestResponse = leaveRequestService.reject(id)
}
