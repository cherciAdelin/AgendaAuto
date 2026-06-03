package com.agenda.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agenda.data.dao.StudentDao
import com.agenda.data.model.Student
import com.agenda.data.model.StudentStatus
import com.agenda.data.settings.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime

enum class SortOption {
    NAME_ASC,
    NAME_DESC,
    MONEY_DESC,
    MONEY_ASC,
    LESSONS_DESC,
    LESSONS_ASC
}
enum class FilterOption { ALL, ACTIV, ADMIS }

class StudentViewModel(
    application: Application,
    private val studentDao: StudentDao
) : AndroidViewModel(application) {

    private val settingsManager = SettingsManager(application)

    val searchQuery = MutableStateFlow("")

    val sortOption = MutableStateFlow(SortOption.NAME_ASC)
    val filterOption = MutableStateFlow(FilterOption.ALL)

    private val _allStudents = studentDao.getAllStudents()

    val studentsState: StateFlow<List<Student>> = combine(
        _allStudents,
        searchQuery,
        sortOption,
        filterOption
    ) { students, query, sort, filter ->

        var processedList = when (filter) {
            FilterOption.ALL -> students
            FilterOption.ACTIV -> students.filter { it.status == StudentStatus.ACTIV }
            FilterOption.ADMIS -> students.filter { it.status == StudentStatus.ADMIS }
        }

        if (query.isNotBlank()) {
            processedList = processedList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        (it.phoneNumber != null && it.phoneNumber.contains(query))
            }
        }

        when (sort) {
            SortOption.NAME_ASC -> processedList.sortedBy { it.name.lowercase() }
            SortOption.NAME_DESC -> processedList.sortedByDescending { it.name.lowercase() }
            SortOption.MONEY_DESC -> processedList.sortedByDescending { it.totalContractValue - it.moneyPaid }
            SortOption.MONEY_ASC -> processedList.sortedBy { it.totalContractValue - it.moneyPaid }
            SortOption.LESSONS_DESC -> processedList.sortedByDescending { it.lessonsTaken }
            SortOption.LESSONS_ASC -> processedList.sortedBy { it.lessonsTaken }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSortOption(option: SortOption) {
        sortOption.value = option
    }

    fun setFilterOption(option: FilterOption) {
        filterOption.value = option
    }

    fun insertStudent(student: Student) {
        viewModelScope.launch {
            try {
                studentDao.insertStudent(student)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            try {
                val updatedStudent = student.copy(lastAccessedMillis = System.currentTimeMillis())
                studentDao.updateStudent(updatedStudent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            try {
                studentDao.deleteStudent(student)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun trackStudentAccess(student: Student) {
        viewModelScope.launch {
            try {
                val updatedStudent = student.copy(lastAccessedMillis = System.currentTimeMillis())
                studentDao.updateStudent(updatedStudent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val isDarkMode = settingsManager.isDarkMode
    val defaultContractValue = settingsManager.defaultContractValue

    fun setDarkMode(isDark: Boolean) {
        settingsManager.setDarkMode(isDark)
    }

    fun setDefaultContractValue(value: Int) {
        settingsManager.setDefaultContractValue(value)
    }

    class LocalDateAdapter : TypeAdapter<LocalDate>() {
        override fun write(out: JsonWriter, value: LocalDate?) {
            if (value == null) out.nullValue() else out.value(value.toString())
        }
        override fun read(input: JsonReader): LocalDate? {
            val stringValue = input.nextString()
            return if (stringValue.isNullOrEmpty()) null else LocalDate.parse(stringValue)
        }
    }

    class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {
        override fun write(out: JsonWriter, value: LocalDateTime?) {
            if (value == null) out.nullValue() else out.value(value.toString())
        }
        override fun read(input: JsonReader): LocalDateTime? {
            val stringValue = input.nextString()
            return if (stringValue.isNullOrEmpty()) null else LocalDateTime.parse(stringValue)
        }
    }

    suspend fun exportDatabaseToJson(): String {
        val allStudents = _allStudents.first()

        val gson = com.google.gson.GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

        return gson.toJson(allStudents)
    }

    fun importDatabaseFromJson(json: String) {
        viewModelScope.launch {
            try {
                val gson = com.google.gson.GsonBuilder()
                    .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
                    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
                    .create()

                val listType = object : com.google.gson.reflect.TypeToken<List<Student>>() {}.type
                val students: List<Student> = gson.fromJson(json, listType)

                students.forEach { studentDao.insertStudent(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearAdmittedStudents() {
        viewModelScope.launch {
            try {
                val allStudents = _allStudents.first()
                val admittedStudents = allStudents.filter { it.status == StudentStatus.ADMIS }
                admittedStudents.forEach { student ->
                    studentDao.deleteStudent(student)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}