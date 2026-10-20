# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Return the task list's D-day label using whole calendar days between `today` and `dueDate`. Completion takes precedence over the due date.

**Pre-conditions**
- `today` is the comparison date; `dueDate` is a calendar date or `null`.

**Post-conditions**
- Success cases
  - A completed task returns `"CLEAR"` with or without a due date. `[Unit]`
    - Tip: Given `today = 2026-10-20` and `isCompleted = true` / When `dueDateLabel` is called with `dueDate = 2026-10-25` and `null` / Then it returns `"CLEAR"` in both cases.
  - An active task without a due date returns `""`. `[Unit]`
    - Tip: Given `today = 2026-10-20`, `dueDate = null`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `""`.
  - An active task due today returns `"D-Day"`. `[Unit]`
    - Tip: Given `today = 2026-10-20`, `dueDate = 2026-10-20`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-Day"`.
  - An active task due in N calendar days returns `"D-N"`. `[Unit]`
    - Tip: Given `today = 2026-10-20`, `dueDate = 2026-10-25`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-5"`.
  - An active task overdue by N calendar days returns `"D+N"`. `[Unit]`
    - Tip: Given `today = 2026-10-20`, `dueDate = 2026-10-15`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+5"`.
- Error cases
  - None. Every valid `LocalDate`, nullable due date, and completion status has a label.
- Edge cases
  - The labels immediately before, at, and after the due-today boundary are `"D+1"`, `"D-Day"`, and `"D-1"`. `[Unit]`
    - Tip: Given `today = 2026-10-20` and `isCompleted = false` / When `dueDateLabel` is called with `dueDate = 2026-10-19`, `2026-10-20`, and `2026-10-21` / Then it returns `"D+1"`, `"D-Day"`, and `"D-1"`, respectively.
  - Calendar day counting continues across month end. `[Unit]`
    - Tip: Given `today = 2026-10-31` and `isCompleted = false` / When `dueDateLabel` is called with `dueDate = 2026-10-30`, `2026-10-31`, and `2026-11-01` / Then it returns `"D+1"`, `"D-Day"`, and `"D-1"`, respectively.
  - Calendar day counting continues across year end. `[Unit]`
    - Tip: Given `today = 2026-12-31` and `isCompleted = false` / When `dueDateLabel` is called with `dueDate = 2026-12-30`, `2026-12-31`, and `2027-01-01` / Then it returns `"D+1"`, `"D-Day"`, and `"D-1"`, respectively.
  - Leap day counts as one calendar day on either side. `[Unit]`
    - Tip: Given `today = 2024-02-29` and `isCompleted = false` / When `dueDateLabel` is called with `dueDate = 2024-02-28`, `2024-02-29`, and `2024-03-01` / Then it returns `"D+1"`, `"D-Day"`, and `"D-1"`, respectively.

**Undefined behavior** (optional)
- None.
