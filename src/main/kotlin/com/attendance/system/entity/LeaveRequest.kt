package com.attendance.system.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "leave_requests")
class LeaveRequest(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    var employee: Employee,
    @Column(nullable = false)
    var startDate: LocalDate,
    @Column(nullable = false)
    var endDate: LocalDate,
    var reason: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: LeaveStatus = LeaveStatus.PENDING,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var createdAt: Instant = Instant.now()
}
