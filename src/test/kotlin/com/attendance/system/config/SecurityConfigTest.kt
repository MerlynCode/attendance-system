package com.attendance.system.config

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest(
    @Autowired val mockMvc: MockMvc,
) {
    @Test
    fun `unauthenticated request to protected api is rejected`() {
        mockMvc.get("/api/admin/leave-requests").andExpect { status { isUnauthorized() } }
    }

    @Test
    @WithMockUser(roles = ["EMPLOYEE"])
    fun `employee cannot access admin endpoints`() {
        mockMvc.get("/api/admin/leave-requests").andExpect { status { isForbidden() } }
    }

    @Test
    fun `swagger ui is publicly accessible`() {
        mockMvc.get("/v3/api-docs").andExpect { status { isOk() } }
    }
}
