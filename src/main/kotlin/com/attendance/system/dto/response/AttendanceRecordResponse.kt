package com.attendance.system.dto.response

import com.attendance.system.entity.AttendanceRecord
import java.time.Instant

data class AttendanceRecordResponse(
    val id: Long,
    val checkInTime: Instant,
    val checkOutTime: Instant?,
) {
    companion object {
        fun from(record: AttendanceRecord) =
            AttendanceRecordResponse(
                id = requireNotNull(record.id),
                checkInTime = record.checkInTime,
                checkOutTime = record.checkOutTime,
            )
    }
}
