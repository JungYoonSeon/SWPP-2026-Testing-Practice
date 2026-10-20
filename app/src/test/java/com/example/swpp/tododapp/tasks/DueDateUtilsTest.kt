package com.example.swpp.tododapp.tasks

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class DueDateUtilsTest {
    private fun date(value: String): LocalDate = LocalDate.parse(value)

    @Test
    fun dueDateLabel_completedWithPastPresentFutureOrNullDate_returnsClear() {
        // Given a completed task and a fixed comparison date.
        val today = date("2026-10-20")

        // When its due date is past, present, future, or absent, then completion takes precedence.
        assertEquals("CLEAR", dueDateLabel(date("2026-10-15"), today, true))
        assertEquals("CLEAR", dueDateLabel(today, today, true))
        assertEquals("CLEAR", dueDateLabel(date("2026-10-25"), today, true))
        assertEquals("CLEAR", dueDateLabel(null, today, true))
    }

    @Test
    fun dueDateLabel_activeWithoutDueDate_returnsEmpty() {
        // Given an active task without a due date.
        val today = date("2026-10-20")

        // When the label is calculated, then nothing is shown.
        assertEquals("", dueDateLabel(null, today, false))
    }

    @Test
    fun dueDateLabel_dueToday_returnsDDay() {
        // Given an active task due on the comparison date.
        val today = date("2026-10-20")

        // When the label is calculated, then it says D-Day.
        assertEquals("D-Day", dueDateLabel(today, today, false))
    }

    @Test
    fun dueDateLabel_dueInFiveDays_returnsDMinusFive() {
        // Given an active task due five calendar days after today.
        val today = date("2026-10-20")

        // When the label is calculated, then it shows five days remaining.
        assertEquals("D-5", dueDateLabel(date("2026-10-25"), today, false))
    }

    @Test
    fun dueDateLabel_overdueByFiveDays_returnsDPlusFive() {
        // Given an active task due five calendar days before today.
        val today = date("2026-10-20")

        // When the label is calculated, then it shows five days overdue.
        assertEquals("D+5", dueDateLabel(date("2026-10-15"), today, false))
    }

    @Test
    fun dueDateLabel_datesAroundToday_returnAdjacentLabels() {
        // Given the three dates around the due-today boundary.
        val today = date("2026-10-20")

        // When each label is calculated, then the labels change at today.
        assertEquals("D+1", dueDateLabel(date("2026-10-19"), today, false))
        assertEquals("D-Day", dueDateLabel(today, today, false))
        assertEquals("D-1", dueDateLabel(date("2026-10-21"), today, false))
    }

    @Test
    fun dueDateLabel_datesAroundMonthEnd_returnAdjacentLabels() {
        // Given dates on either side of October's last day.
        val today = date("2026-10-31")

        // When each label is calculated, then November 1 is one day away.
        assertEquals("D+1", dueDateLabel(date("2026-10-30"), today, false))
        assertEquals("D-Day", dueDateLabel(today, today, false))
        assertEquals("D-1", dueDateLabel(date("2026-11-01"), today, false))
    }

    @Test
    fun dueDateLabel_datesAroundYearEnd_returnAdjacentLabels() {
        // Given dates on either side of December's last day.
        val today = date("2026-12-31")

        // When each label is calculated, then January 1 is one day away.
        assertEquals("D+1", dueDateLabel(date("2026-12-30"), today, false))
        assertEquals("D-Day", dueDateLabel(today, today, false))
        assertEquals("D-1", dueDateLabel(date("2027-01-01"), today, false))
    }

    @Test
    fun dueDateLabel_threeDaysAcrossMonthEnd_returnsDMinusThree() {
        // Given a due date three days after October 30.
        val today = date("2026-10-30")

        // When the label is calculated, then the full interval is counted.
        assertEquals("D-3", dueDateLabel(date("2026-11-02"), today, false))
    }

    @Test
    fun dueDateLabel_threeDaysBeforeAcrossMonthEnd_returnsDPlusThree() {
        // Given a due date three days before November 2.
        val today = date("2026-11-02")

        // When the label is calculated, then the full interval is counted.
        assertEquals("D+3", dueDateLabel(date("2026-10-30"), today, false))
    }

    @Test
    fun dueDateLabel_thirtySixDaysAcrossMonth_returnsDMinusThirtySix() {
        // Given a due date in the next month, 36 days later.
        val today = date("2026-10-20")

        // When the label is calculated, then the month's days are included.
        assertEquals("D-36", dueDateLabel(date("2026-11-25"), today, false))
    }

    @Test
    fun dueDateLabel_thirtySixDaysBeforeAcrossMonth_returnsDPlusThirtySix() {
        // Given a due date in the previous month, 36 days earlier.
        val today = date("2026-11-25")

        // When the label is calculated, then the month's days are included.
        assertEquals("D+36", dueDateLabel(date("2026-10-20"), today, false))
    }

    @Test
    fun dueDateLabel_datesAroundLeapDay_returnAdjacentLabels() {
        // Given dates on either side of February 29.
        val today = date("2024-02-29")

        // When each label is calculated, then leap day has adjacent dates.
        assertEquals("D+1", dueDateLabel(date("2024-02-28"), today, false))
        assertEquals("D-Day", dueDateLabel(today, today, false))
        assertEquals("D-1", dueDateLabel(date("2024-03-01"), today, false))
    }

    @Test
    fun dueDateLabel_threeDaysAcrossYearEnd_returnsDMinusThree() {
        // Given a due date three days after December 30.
        val today = date("2026-12-30")

        // When the label is calculated, then the full interval is counted.
        assertEquals("D-3", dueDateLabel(date("2027-01-02"), today, false))
    }

    @Test
    fun dueDateLabel_threeDaysBeforeAcrossYearEnd_returnsDPlusThree() {
        // Given a due date three days before January 2.
        val today = date("2027-01-02")

        // When the label is calculated, then the full interval is counted.
        assertEquals("D+3", dueDateLabel(date("2026-12-30"), today, false))
    }

    @Test
    fun dueDateLabel_full2026YearForward_returnsDMinus365() {
        // Given the reviewed spec's full-year forward example.
        val today = date("2026-01-01")

        // When the label is calculated, then use the exact reviewed expectation.
        assertEquals("D-365", dueDateLabel(date("2027-01-01"), today, false))
    }

    @Test
    fun dueDateLabel_full2026YearBackward_returnsDPlus365() {
        // Given the same full-year interval in reverse.
        val today = date("2027-01-01")

        // When the label is calculated, then it shows 365 days overdue.
        assertEquals("D+365", dueDateLabel(date("2026-01-01"), today, false))
    }

    @Test
    fun dueDateLabel_twoDaysForwardAcrossLeapDay_returnsDMinusTwo() {
        // Given February 28 and a due date after leap day.
        val today = date("2024-02-28")

        // When the label is calculated, then February 29 is included.
        assertEquals("D-2", dueDateLabel(date("2024-03-01"), today, false))
    }

    @Test
    fun dueDateLabel_twoDaysBackwardAcrossLeapDay_returnsDPlusTwo() {
        // Given March 1 and a due date before leap day.
        val today = date("2024-03-01")

        // When the label is calculated, then February 29 is included.
        assertEquals("D+2", dueDateLabel(date("2024-02-28"), today, false))
    }
}
