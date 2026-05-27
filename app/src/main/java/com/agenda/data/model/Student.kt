package com.agenda.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

enum class StudentStatus{
    ACTIV,
    ADMIS
}

@Entity(tableName = "students")
data class Student (
    @PrimaryKey(autoGenerate = true) val studentId: Int = 0,
    val name: String,
    val phoneNumber: String?,
    val lessonsTaken: Int,
    val upcomingLesson: LocalDateTime?,
    val upcomingExam: LocalDate?,
    val totalContractValue: Int,
    val moneyPaid: Int,
    val status: StudentStatus = StudentStatus.ACTIV,
    val lastAccessedMillis: Long = System.currentTimeMillis()
) {
    val moneyToBePaid: Int
        get() = totalContractValue - moneyPaid
}