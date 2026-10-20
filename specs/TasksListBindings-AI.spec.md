# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `fun setDueDateLabel(textView: TextView, task: Task?): Unit`

**Intent**: Show the current task's D-day label using the current local calendar date. Hide the view when the task has no label, including when a reused view previously displayed another task's label.

**Pre-conditions**
- `textView` is a valid task list label view; `task` may be `null`.
- The device's local calendar date is used as `today` for this binding call.

**Post-conditions**
- Success cases
  - An active task with a due date displays its current D-day label in a visible view. `[Integration]`
    - Tip: Given the local date is `2026-10-20`, an active task due `2026-10-25`, and a `TextView` / When `setDueDateLabel` is called / Then the view text is `"D-5"` and its visibility is `View.VISIBLE`.
  - A completed task displays `"CLEAR"` in a visible view, with or without a due date. `[Integration]`
    - Tip: Given the local date is `2026-10-20`, a completed task with `dueDate = null`, and a `TextView` / When `setDueDateLabel` is called / Then the view text is `"CLEAR"` and its visibility is `View.VISIBLE`.
  - An active task without a due date leaves no label and hides the view. `[Integration]`
    - Tip: Given the local date is `2026-10-20`, an active task with `dueDate = null`, and a `TextView` initially showing `"D-5"` with `View.VISIBLE` / When `setDueDateLabel` is called / Then the view text is `""` and its visibility is `View.GONE`.
  - A null task leaves no label and hides the view. `[Integration]`
    - Tip: Given the local date is `2026-10-20`, `task = null`, and a `TextView` initially showing `"CLEAR"` with `View.VISIBLE` / When `setDueDateLabel` is called / Then the view text is `""` and its visibility is `View.GONE`.
- Error cases
  - None. A null task is treated as a missing label.
- Edge cases
  - Due dates immediately before, at, and after the current date display overdue, due-today, and upcoming labels. `[Integration]`
    - Tip: Given the local date is `2026-10-20` and an active task / When `setDueDateLabel` is called with task due dates `2026-10-19`, `2026-10-20`, and `2026-10-21` / Then the view text is `"D+1"`, `"D-Day"`, and `"D-1"`, respectively, with `View.VISIBLE` after each call.
  - Rebinding a hidden view to a task with a label restores its visibility. `[Integration]`
    - Tip: Given the local date is `2026-10-20` and a `TextView` with text `""` and `View.GONE` / When `setDueDateLabel` is called with an active task due `2026-10-21` / Then the view text is `"D-1"` and its visibility is `View.VISIBLE`.
  - Rebinding a labeled view to a task without a label clears the previous label. `[Integration]`
    - Tip: Given the local date is `2026-10-20` and a `TextView` showing `"D-Day"` with `View.VISIBLE` / When `setDueDateLabel` is called with an active task whose `dueDate = null` / Then the view text is `""` and its visibility is `View.GONE`.

**Undefined behavior** (optional)
- None.
