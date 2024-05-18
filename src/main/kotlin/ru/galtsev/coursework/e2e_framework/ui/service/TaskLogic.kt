package ru.galtsev.coursework.e2e_framework.ui.service

import com.github.kagkarlsson.scheduler.ScheduledExecution
import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.task.TaskInstanceId
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import ru.galtsev.coursework.e2e_framework.ui.dto.*
import ru.galtsev.coursework.e2e_framework.ui.mapper.TaskMapper
import java.time.Instant
import java.util.function.Consumer
import java.util.stream.Collectors


@Service
class TaskLogic(private val scheduler: Scheduler, private val caching: TaskCaching) {
    private val showData: Boolean = true

    init {
        scheduler.start()
    }

    fun runTaskNow(taskId: String, taskName: String, scheduleTime: Instant?) {
        val scheduledExecutionOpt =
            scheduler.getScheduledExecution(TaskInstanceId.of(taskName, taskId))

        if (scheduledExecutionOpt.isPresent && !scheduledExecutionOpt.get().isPicked) {
            scheduler.reschedule(
                scheduledExecutionOpt.get().taskInstance,
                scheduleTime ?: Instant.now()
            )
        } else {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No ScheduledExecution found for taskName: $taskName, taskId: $taskId"
            )
        }
    }

    fun runTaskGroupNow(taskName: String, onlyFailed: Boolean) {
        caching
            .getExecutionsFromCacheOrDB(false, scheduler)
            .forEach {
                Consumer { execution: ScheduledExecution<Any?> ->
                    if (((!onlyFailed || execution.consecutiveFailures > 0)
                                && (taskName == execution.taskInstance.taskName))
                    ) {
                        try {
                            runTaskNow(
                                execution.taskInstance.id,
                                execution.taskInstance.taskName,
                                Instant.now()
                            )
                        } catch (e: ResponseStatusException) {
                            println("Failed to run task: " + e.message)
                        }
                    }
                }
            }
    }

    fun deleteTask(taskId: String, taskName: String) {
        val scheduledExecutionOpt =
            scheduler.getScheduledExecution(TaskInstanceId.of(taskName, taskId))

        if (scheduledExecutionOpt.isPresent) {
            val taskInstance = scheduledExecutionOpt.get().taskInstance
            scheduler.cancel(taskInstance)
        } else {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No ScheduledExecution found for taskName: $taskName, taskId: $taskId"
            )
        }
    }

    fun getAllTasks(params: TaskRequestParams): GetTasksResponse {
        val executions =
            caching.getExecutionsFromCacheOrDB(params.isRefresh, scheduler)

        var tasks = TaskMapper.mapAllExecutionsToTaskModelUngrouped(executions)

//        tasks =
//            QueryUtils.searchByTaskName(
//                tasks, params.searchTermTaskName, params.isTaskNameExactMatch
//            )
//        tasks =
//            QueryUtils.searchByTaskInstance(
//                tasks, params.searchTermTaskInstance, params.isTaskInstanceExactMatch
//            )
        if (!showData) {
            tasks.forEach(Consumer { e: TaskModel -> e.taskData = listOf() })
        }
        tasks = TaskMapper.groupTasks(tasks)
//        tasks =
//            QueryUtils.sortTasks(
//                QueryUtils.filterTasks(tasks, params.filter), params.sorting, params.isAsc
//            )
//        val pagedTasks =
//            QueryUtils.paginate(tasks, params.pageNumber, params.size)
        val pagedTasks = listOf<TaskModel>()
        return GetTasksResponse(tasks.size, pagedTasks, params.size)
    }

    fun getTask(params: TaskDetailsRequestParams): GetTasksResponse {
        val executions =
            caching.getExecutionsFromCacheOrDB(params.isRefresh, scheduler)

        var tasks =
            if (params.taskId != null
            ) TaskMapper.mapAllExecutionsToTaskModelUngrouped(executions).stream()
                .filter { task: TaskModel ->
                    ((task.taskName == params.taskName) && (task.taskInstance?.get(0) == params.taskId))
                }
                .collect(Collectors.toList())
            else TaskMapper.mapAllExecutionsToTaskModelUngrouped(executions).stream()
                .filter { task: TaskModel -> (task.taskName == params.taskName) }
                .collect(Collectors.toList())
        if (tasks.isEmpty()) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No tasks found for taskName: "
                        + params.taskName
                        + ", taskId: "
                        + params.taskId
            )
        }
//        tasks =
//            QueryUtils.searchByTaskName(
//                tasks, params.searchTermTaskName, params.isTaskNameExactMatch
//            )
//        tasks =
//            QueryUtils.searchByTaskInstance(
//                tasks, params.searchTermTaskInstance, params.isTaskInstanceExactMatch
//            )
//        tasks =
//            QueryUtils.sortTasks(
//                QueryUtils.filterTasks(tasks, params.filter), params.sorting, params.isAsc
//            )
//        if (!showData) {
//            val list: ArrayList<Any> = listOf()
//            tasks.forEach(Consumer { e: TaskModel -> e.taskData = list })
//        }

//        val pagedTasks =
//            QueryUtils.paginate(tasks, params.pageNumber, params.size)
        val pagedTasks: List<TaskModel> = listOf()
        return GetTasksResponse(tasks.size, pagedTasks, params.size)
    }

    fun pollTasks(params: TaskDetailsRequestParams): PollResponse {
//        val allTasks =
//            QueryUtils.filterExecutions(
//                caching.getExecutionsFromDBWithoutUpdatingCache(scheduler),
//                TaskRequestParams.TaskFilter.ALL,
//                params.taskName
//            )

        val allTasks = listOf<ScheduledExecution<Any?>>()

        val newTaskNames: MutableSet<String> = HashSet()
        val newFailureTaskNames: MutableSet<String> = HashSet()
        val newRunningTaskNames: MutableSet<String> = HashSet()

        val stoppedFailing = 0
        val finishedRunning = 0

        for (task: ScheduledExecution<Any?> in allTasks) {
            val taskName = task.taskInstance.taskName
            val status = getStatus(task)
            val cachedStatus = caching.getStatusFromCache(task.taskInstance)

            if (cachedStatus == null) {
                handleNewTask(
                    params, newTaskNames, newFailureTaskNames, newRunningTaskNames, taskName, status
                )
                continue
            }

            if (cachedStatus != status) {
                handleStatusChange(
                    params,
                    newFailureTaskNames,
                    newRunningTaskNames,
                    taskName,
                    status,
                    cachedStatus,
                    stoppedFailing,
                    finishedRunning
                )
            }
        }

        return PollResponse(
            newFailureTaskNames.size,
            newRunningTaskNames.size,
            newTaskNames.size,
            stoppedFailing,
            finishedRunning
        )
    }

    private fun handleNewTask(
        params: TaskDetailsRequestParams,
        newTaskNames: MutableSet<String>,
        newFailureTaskNames: MutableSet<String>,
        newRunningTaskNames: MutableSet<String>,
        taskName: String,
        status: String
    ) {
        if (newTaskNames.contains(taskName) && params.taskName == null) return

        newTaskNames.add(taskName)
        if (status[0] == '1') newFailureTaskNames.add(taskName)
        if (status[1] == '1') newRunningTaskNames.add(taskName)
    }

    private fun handleStatusChange(
        params: TaskDetailsRequestParams,
        newFailureTaskNames: MutableSet<String>,
        newRunningTaskNames: MutableSet<String>,
        taskName: String,
        status: String,
        cachedStatus: String,
        stoppedFailing: Int,
        finishedRunning: Int
    ) {
        var stoppedFailing = stoppedFailing
        var finishedRunning = finishedRunning
        if ((cachedStatus[0] == '0'
                    ) && (status[0] == '1'
                    ) && (!newFailureTaskNames.contains(taskName) || params.taskName != null)
        ) {
            newFailureTaskNames.add(taskName)
        }
        if (cachedStatus[0] == '1' && status[0] == '0') stoppedFailing++

        if ((cachedStatus[1] == '0'
                    ) && (status[1] == '1'
                    ) && (!newRunningTaskNames.contains(taskName) || params.taskName != null)
        ) {
            newRunningTaskNames.add(taskName)
        }
        if (cachedStatus[1] == '1' && status[1] == '0') finishedRunning++
    }

    private fun getStatus(task: ScheduledExecution<Any?>): String {
        return ((if (task.consecutiveFailures > 0) "1" else "0")
                + (if (task.pickedBy != null) "1" else "0"))
    }
}