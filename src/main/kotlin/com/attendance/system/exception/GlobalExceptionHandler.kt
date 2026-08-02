package com.attendance.system.exception

import com.attendance.system.dto.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(
        AlreadyCheckedInException::class,
        NoActiveCheckInException::class,
        OverlappingLeaveRequestException::class,
    )
    fun handleConflict(ex: RuntimeException): ResponseEntity<ErrorResponse> = respond(HttpStatus.CONFLICT, ex.message)

    @ExceptionHandler(EmployeeNotFoundException::class, LeaveRequestNotFoundException::class)
    fun handleNotFound(ex: RuntimeException): ResponseEntity<ErrorResponse> = respond(HttpStatus.NOT_FOUND, ex.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return respond(HttpStatus.BAD_REQUEST, message)
    }

    @ExceptionHandler(InvalidLeaveDateRangeException::class)
    fun handleInvalidDateRange(ex: InvalidLeaveDateRangeException): ResponseEntity<ErrorResponse> =
        respond(HttpStatus.BAD_REQUEST, ex.message)

    private fun respond(
        status: HttpStatus,
        message: String?,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(status).body(
            ErrorResponse(status = status.value(), error = status.reasonPhrase, message = message),
        )
}
