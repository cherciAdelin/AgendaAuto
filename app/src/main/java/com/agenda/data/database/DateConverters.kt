package com.agenda.data.database

import androidx.room.TypeConverter
import com.agenda.data.model.StudentStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateConverters {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        if (value.isNullOrBlank()) return null
        return try {
            LocalDate.parse(value, dateFormatter)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    fun fromDateTimeTimestamp(value: String?): LocalDateTime? {
        if (value.isNullOrBlank()) return null
        return try {
            LocalDateTime.parse(value, dateTimeFormatter)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun dateTimeToTimestamp(date: LocalDateTime?): String? {
        return date?.format(dateTimeFormatter)
    }

    @TypeConverter
    fun fromStatusString(value: String?): StudentStatus {
        return try {
            if (value != null) StudentStatus.valueOf(value) else StudentStatus.ACTIV
        } catch (e: Exception) {
            StudentStatus.ACTIV
        }
    }

    @TypeConverter
    fun statusToString(status: StudentStatus?): String {
        return status?.name ?: StudentStatus.ACTIV.name
    }
}