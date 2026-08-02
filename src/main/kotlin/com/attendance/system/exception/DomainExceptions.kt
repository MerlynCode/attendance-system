package com.attendance.system.exception

class AlreadyCheckedInException(
    employeeEmail: String,
) : RuntimeException("Employee $employeeEmail already has an open check-in")

class NoActiveCheckInException(
    employeeEmail: String,
) : RuntimeException("Employee $employeeEmail has no active check-in to check out from")

class EmployeeNotFoundException(
    identifier: String,
) : RuntimeException("No employee found for '$identifier'")

class OverlappingLeaveRequestException(
    employeeEmail: String,
) : RuntimeException("Employee $employeeEmail already has a pending or approved leave request overlapping these dates")

class LeaveRequestNotFoundException(
    id: Long,
) : RuntimeException("No leave request found with id $id")

class InvalidLeaveDateRangeException : RuntimeException("Leave request end date must not be before the start date")
