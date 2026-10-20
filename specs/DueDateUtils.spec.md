# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Return the task list's D-day label using whole calendar days between `today` and `dueDate`,including differences spanning months, years, and leap days. Completion takes precedence over the due date.

**Pre-conditions**
- `today` is the comparison date; `dueDate` is a calendar date or `null`.
- isCompleted indicates whether the task is completed.
-  The number of calendar days is calculated using the complete dates, not individual date components. 

**Post-conditions**
- Success cases
  - A completed task returns `"CLEAR"` with or without a due date. It doesn't matter if the due date is in the past, present or in the future. `[Unit]`
    - Tip: Given `today = 2026-10-20` and `isCompleted = true` / When `dueDateLabel` is called with `dueDate = 2026-10-15`, `2026-10-20`, `2026-10-25`, and `null` / Then it returns `"CLEAR"` in all four cases.
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
  - Calendar day counting continues across month end, including the dates before and after the boundary. `[Unit]`
    - Tip: Given `today = 2026-10-31` and `isCompleted = false` / When `dueDateLabel` is called with `dueDate = 2026-10-30`, `2026-10-31`, and `2026-11-01` / Then it returns `"D+1"`, `"D-Day"`, and `"D-1"`, respectively.
  - Calendar day counting continues across year end, including the dates before and after the boundary. `[Unit]`
    - Tip: Given `today = 2026-12-31` and `isCompleted = false` / When `dueDateLabel` is called with `dueDate = 2026-12-30`, `2026-12-31`, and `2027-01-01` / Then it returns `"D+1"`, `"D-Day"`, and `"D-1"`, respectively.
  - Calendar day differences spanning multiple days across a month boundary must use the total number of calendar days, rather than only the day-of-month difference. `[Unit]`
    - Tip: Given `today = 2026-10-30` and `isCompleted = false` / When `dueDateLabel` is called with `dueDate = 2026-11-02` / Then it returns `"D-3"`.
    - Tip : Given `today = 2026-11-02`, `dueDate = 2026-10-30`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+3"`.
  - Calendar day differences spanning one or more complete months must not discard the days contributed by those months. [Unit]
    - Tip: Given `today = 2026-10-20`, `dueDate = 2026-11-25`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-36"`.
    - Tip: Given today = 2026-11-25, dueDate = 2026-10-20, and isCompleted = false / When dueDateLabel is called / Then it returns "D+36".
  - Leap day counts as one calendar day on either side. `[Unit]`
    - Tip: Given `today = 2024-02-29` and `isCompleted = false` / When `dueDateLabel` is called with `dueDate = 2024-02-28`, `2024-02-29`, and `2024-03-01` / Then it returns `"D+1"`, `"D-Day"`, and `"D-1"`, respectively.
  - Calendar day differences spanning multiple days across a year boundary must use the total calender-day difference. `[Unit]`
    - Tip: Given `today = 2026-12-30`, `dueDate = 2027-01-02`, and `isCompleted = false` / When `dueDateLabel` is called/ Then it returns `"D-3"`.
    - Tip : Given `today = 2027-01-02`, `dueDate = 2026-12-30`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+3"`.
  - Calendar day differences spanning a full year must include all calendar days in that interval. `[Unit]`
    - Tip: Given `today = 2026-01-01`, `dueDate = 2027-01-01`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-365"`.
    - Tip: Given today = `2027-01-01`, `dueDate = 2026-01-01`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+365"`.
  - Calendar day differences spanning leap day must include February 29 in the total day count. `[Unit]`
    - Tip: Given `today = 2024-02-28`, `dueDate = 2024-03-01`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-2`.
    - Tip: Given `today = 2024-03-01`, `dueDate = 2024-02-28`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+2"`.

**Undefined behavior** (optional)
- None.
