package ru.galtsev.coursework.e2e_framework.ui.dto

import java.time.Instant


class TaskDetailsRequestParams(
    filter: TaskFilter?,
    pageNumber: Int?,
    size: Int?,
    sorting: TaskSort?,
    asc: Boolean?,
    searchTermTaskName: String?,
    searchTermTaskInstance: String?,
    taskNameExactMatch: Boolean?,
    taskInstanceExactMatch: Boolean?,
    startTime: Instant?,
    endTime: Instant?,
    val taskName: String,
    val taskId: String,
    refresh: Boolean?
) : TaskRequestParams(
    filter,
    pageNumber,
    size,
    sorting,
    asc,
    searchTermTaskName!!,
    searchTermTaskInstance!!,
    taskNameExactMatch,
    taskInstanceExactMatch,
    startTime,
    endTime,
    refresh
)