package com.attendance.system

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AttendanceSystemApplication

fun main(args: Array<String>) {
    runApplication<AttendanceSystemApplication>(*args)
}
