package com.agenda.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agenda.ui.components.calendar.CalendarGrid
import com.agenda.ui.components.calendar.CalendarHeader
import com.agenda.ui.components.calendar.DailyDetails
import com.agenda.ui.components.calendar.DaysOfWeekHeader
import com.agenda.viewmodel.StudentViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.studentsState.collectAsState()

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    val lessonsByDate = remember(students) {
        students.filter { it.upcomingLesson != null }
            .groupBy { it.upcomingLesson!!.toLocalDate() }
    }

    val examsByDate = remember(students) {
        students.filter { it.upcomingExam != null }
            .groupBy { it.upcomingExam!! }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CalendarHeader(
            currentMonth = currentMonth,
            onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
            onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DaysOfWeekHeader()

        Spacer(modifier = Modifier.height(8.dp))

        CalendarGrid(
            currentMonth = currentMonth,
            lessonsByDate = lessonsByDate,
            examsByDate = examsByDate,
            onDateSelected = { selectedDate = it }
        )
    }

    if (selectedDate != null) {
        DailyDetails(
            date = selectedDate!!,
            lessons = lessonsByDate[selectedDate] ?: emptyList(),
            exams = examsByDate[selectedDate] ?: emptyList(),
            onDismiss = { selectedDate = null }
        )
    }
}

