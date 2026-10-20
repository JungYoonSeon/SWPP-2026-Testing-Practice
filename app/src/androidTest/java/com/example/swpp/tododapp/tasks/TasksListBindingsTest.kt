package com.example.swpp.tododapp.tasks

import android.view.View
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.data.Task
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {
    private fun withTextView(block: (TextView) -> Unit) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            block(TextView(instrumentation.targetContext))
        }
    }

    private fun assertLabel(textView: TextView, expectedText: String, expectedVisibility: Int) {
        assertEquals(expectedText, textView.text.toString())
        assertEquals(expectedVisibility, textView.visibility)
    }

    @Test
    fun setDueDateLabel_activeTaskDueInFiveDays_showsDMinusFive() = withTextView { textView ->
        // Given an active task due five local calendar days from today.
        val task = Task(dueDate = LocalDate.now().plusDays(5))

        // When the binding is applied, then the label is visible.
        setDueDateLabel(textView, task)
        assertLabel(textView, "D-5", View.VISIBLE)
    }

    @Test
    fun setDueDateLabel_completedTaskWithoutDueDate_showsClear() = withTextView { textView ->
        // Given a completed task without a due date.
        val task = Task(isCompleted = true, dueDate = null)

        // When the binding is applied, then completion is visible.
        setDueDateLabel(textView, task)
        assertLabel(textView, "CLEAR", View.VISIBLE)
    }

    @Test
    fun setDueDateLabel_activeTaskWithoutDueDate_clearsAndHidesView() = withTextView { textView ->
        // Given a visible label left by a previous task.
        textView.text = "D-5"
        textView.visibility = View.VISIBLE
        val task = Task(isCompleted = false, dueDate = null)

        // When an undated active task is bound, then no old label remains.
        setDueDateLabel(textView, task)
        assertLabel(textView, "", View.GONE)
    }

    @Test
    fun setDueDateLabel_nullTask_clearsAndHidesView() = withTextView { textView ->
        // Given a visible completion label left by a previous task.
        textView.text = "CLEAR"
        textView.visibility = View.VISIBLE

        // When a null task is bound, then no old label remains.
        setDueDateLabel(textView, null)
        assertLabel(textView, "", View.GONE)
    }

    @Test
    fun setDueDateLabel_datesAroundToday_showAdjacentLabels() = withTextView { textView ->
        // Given active tasks due yesterday, today, and tomorrow.
        val today = LocalDate.now()

        // When each task is bound, then its boundary label is visible.
        setDueDateLabel(textView, Task(dueDate = today.minusDays(1)))
        assertLabel(textView, "D+1", View.VISIBLE)
        setDueDateLabel(textView, Task(dueDate = today))
        assertLabel(textView, "D-Day", View.VISIBLE)
        setDueDateLabel(textView, Task(dueDate = today.plusDays(1)))
        assertLabel(textView, "D-1", View.VISIBLE)
    }

    @Test
    fun setDueDateLabel_hiddenViewThenDatedTask_restoresVisibility() = withTextView { textView ->
        // Given a hidden, empty view and an active task due tomorrow.
        textView.text = ""
        textView.visibility = View.GONE
        val task = Task(dueDate = LocalDate.now().plusDays(1))

        // When the task is bound, then its label is shown.
        setDueDateLabel(textView, task)
        assertLabel(textView, "D-1", View.VISIBLE)
    }

    @Test
    fun setDueDateLabel_labeledViewThenUndatedTask_clearsAndHidesView() = withTextView { textView ->
        // Given a visible D-Day label and an undated active task.
        textView.text = "D-Day"
        textView.visibility = View.VISIBLE
        val task = Task(dueDate = null)

        // When the task is bound, then its missing label clears the view.
        setDueDateLabel(textView, task)
        assertLabel(textView, "", View.GONE)
    }

    @Test
    fun setDueDateLabel_completedThenActiveUndatedTask_removesClear() = withTextView { textView ->
        // Given an undated task that is initially completed.
        val task = Task(isCompleted = true, dueDate = null)
        textView.text = ""

        // When completion is bound and then unchecked, then CLEAR is removed.
        setDueDateLabel(textView, task)
        assertLabel(textView, "CLEAR", View.VISIBLE)
        task.isCompleted = false
        setDueDateLabel(textView, task)
        assertLabel(textView, "", View.GONE)
    }
}
