package com.attendance.system.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AttendanceControllerTest(
    @Autowired val mockMvc: MockMvc,
) {
    @Test
    fun `check-in without authentication is rejected`() {
        mockMvc.post("/api/attendance/check-in").andExpect { status { isUnauthorized() } }
    }

    @Test
    @WithMockUser(username = "employee1@company.com", roles = ["EMPLOYEE"])
    fun `employee can check in then check out`() {
        mockMvc.post("/api/attendance/check-in").andExpect { status { isCreated() } }
        mockMvc.post("/api/attendance/check-out").andExpect { status { isOk() } }
    }

    @Test
    @WithMockUser(username = "employee1@company.com", roles = ["EMPLOYEE"])
    fun `checking in twice without checking out is rejected`() {
        mockMvc.post("/api/attendance/check-in").andExpect { status { isCreated() } }
        mockMvc.post("/api/attendance/check-in").andExpect { status { isConflict() } }
    }

    @Test
    @WithMockUser(username = "employee1@company.com", roles = ["EMPLOYEE"])
    fun `checking out without a prior check-in is rejected`() {
        mockMvc.post("/api/attendance/check-out").andExpect { status { isConflict() } }
    }
}
