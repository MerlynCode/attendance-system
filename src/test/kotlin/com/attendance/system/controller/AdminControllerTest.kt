package com.attendance.system.controller

import com.attendance.system.entity.LeaveRequest
import com.attendance.system.repository.EmployeeRepository
import com.attendance.system.repository.LeaveRequestRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val employeeRepository: EmployeeRepository,
    @Autowired val leaveRequestRepository: LeaveRequestRepository,
) {
    @Test
    @WithMockUser(username = "admin@company.com", roles = ["ADMIN"])
    fun `admin can list and approve a pending leave request`() {
        val employee = requireNotNull(employeeRepository.findByEmail("employee1@company.com"))
        val leaveRequest =
            leaveRequestRepository.save(
                LeaveRequest(employee = employee, startDate = LocalDate.of(2026, 11, 1), endDate = LocalDate.of(2026, 11, 5)),
            )

        mockMvc.get("/api/admin/leave-requests?status=PENDING").andExpect { status { isOk() } }

        mockMvc.patch("/api/admin/leave-requests/${leaveRequest.id}/approve").andExpect { status { isOk() } }

        val updated = leaveRequestRepository.findById(requireNotNull(leaveRequest.id)).orElseThrow()
        assertThat(updated.status.name).isEqualTo("APPROVED")
    }

    @Test
    @WithMockUser(username = "admin@company.com", roles = ["ADMIN"])
    fun `approving a non-existent leave request returns 404`() {
        mockMvc.patch("/api/admin/leave-requests/999999/approve").andExpect { status { isNotFound() } }
    }
}
