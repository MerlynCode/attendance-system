package com.attendance.system.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LeaveRequestControllerTest(
    @Autowired val mockMvc: MockMvc,
) {
    @Test
    fun `submitting a leave request without authentication is rejected`() {
        mockMvc
            .post("/api/leave-requests") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"startDate":"2026-09-01","endDate":"2026-09-05"}"""
            }.andExpect { status { isUnauthorized() } }
    }

    @Test
    @WithMockUser(username = "employee2@company.com", roles = ["EMPLOYEE"])
    fun `employee can submit a leave request`() {
        mockMvc
            .post("/api/leave-requests") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"startDate":"2026-09-01","endDate":"2026-09-05","reason":"Vacation"}"""
            }.andExpect { status { isCreated() } }
    }

    @Test
    @WithMockUser(username = "employee2@company.com", roles = ["EMPLOYEE"])
    fun `overlapping leave requests are rejected`() {
        mockMvc
            .post("/api/leave-requests") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"startDate":"2026-09-01","endDate":"2026-09-05"}"""
            }.andExpect { status { isCreated() } }

        mockMvc
            .post("/api/leave-requests") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"startDate":"2026-09-04","endDate":"2026-09-10"}"""
            }.andExpect { status { isConflict() } }
    }

    @Test
    @WithMockUser(username = "employee2@company.com", roles = ["EMPLOYEE"])
    fun `end date before start date is rejected`() {
        mockMvc
            .post("/api/leave-requests") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"startDate":"2026-09-05","endDate":"2026-09-01"}"""
            }.andExpect { status { isBadRequest() } }
    }
}
