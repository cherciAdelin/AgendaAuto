package com.agenda.ui.components.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agenda.data.model.Student
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    lessonsByDate: Map<LocalDate, List<Student>>,
    examsByDate: Map<LocalDate, List<Student>>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1)
    val emptySpacesAtStart = firstDayOfMonth.dayOfWeek.value - 1
    val today = LocalDate.now()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(emptySpacesAtStart) {
            Box(modifier = Modifier.aspectRatio(0.8f))
        }

        items(daysInMonth) { dayOffset ->
            val date = currentMonth.atDay(dayOffset + 1)
            val isToday = date == today
            val lessonsToday = lessonsByDate[date] ?: emptyList()
            val examsToday = examsByDate[date] ?: emptyList()

            CalendarCell(
                date = date,
                isToday = isToday,
                lessonCount = lessonsToday.size,
                examCount = examsToday.size,
                onClick = { onDateSelected(date) }
            )
        }
    }
}