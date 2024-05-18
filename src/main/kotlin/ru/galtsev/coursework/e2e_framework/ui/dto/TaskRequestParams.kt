package ru.galtsev.coursework.e2e_framework.ui.dto

import java.time.Instant
import java.time.temporal.ChronoUnit


open class TaskRequestParams(
    filter: TaskFilter?,
    pageNumber: Int?,
    size: Int?,
    sorting: TaskSort?,
    asc: Boolean?,
    val searchTermTaskName: String,
    val searchTermTaskInstance: String,
    taskNameExactMatch: Boolean?,
    taskInstanceExactMatch: Boolean?,
    startTime: Instant?,
    endTime: Instant?,
    refresh: Boolean?
) {
    val filter: TaskFilter
    val pageNumber: Int
    val size: Int
    val sorting: TaskSort
    val isAsc: Boolean
    var isRefresh: Boolean

    val isTaskNameExactMatch: Boolean

    val isTaskInstanceExactMatch: Boolean
    val startTime: Instant
    val endTime: Instant

    init {
        this.filter = filter ?: TaskFilter.ALL
        this.pageNumber = pageNumber ?: 0
        this.size = size ?: 10
        this.sorting = sorting ?: TaskSort.DEFAULT
        this.isAsc = asc ?: true
        this.isTaskNameExactMatch = taskNameExactMatch ?: false
        this.isTaskInstanceExactMatch = taskInstanceExactMatch ?: false
        this.startTime = startTime ?: Instant.now().minus(50, ChronoUnit.DAYS)
        this.endTime = endTime ?: Instant.now().plus(50, ChronoUnit.DAYS)
        this.isRefresh = refresh ?: true
    }

    enum class TaskFilter {
        ALL,
        FAILED,
        RUNNING,
        SCHEDULED,
        SUCCEEDED
    }

    enum class TaskSort {
        DEFAULT,
        NAME
    }
}