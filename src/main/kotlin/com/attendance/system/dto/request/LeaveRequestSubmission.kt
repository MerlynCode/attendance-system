package com.attendance.system.dto.request

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class LeaveRequestSubmission(
    @field:NotNull
    val startDate: LocalDate?,
    @field:NotNull
    val endDate: LocalDate?,
    @field:Size(max = 500)
    val reason: String?,
)
