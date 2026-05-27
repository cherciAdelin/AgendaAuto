package com.agenda.data.dao

import androidx.room.*
import com.agenda.data.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students ORDER BY lastAccessedMillis DESC LIMIT 5")
    fun getRecentStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    fun searchStudents(searchQuery: String): Flow<List<Student>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
}