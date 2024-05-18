package ru.galtsev.coursework.e2e_framework.ui.mapper

import com.github.kagkarlsson.scheduler.ScheduledExecution
import ru.galtsev.coursework.e2e_framework.ui.dto.TaskModel
import java.time.Instant
import java.util.*
import java.util.stream.Collectors


object TaskMapper {
    fun mapScheduledExecutionsToTaskModel(
        scheduledExecutions: List<ScheduledExecution<Any>>
    ): List<TaskModel> {
        return scheduledExecutions
            .map { execution: ScheduledExecution<Any> ->
                TaskModel(
                    execution.taskInstance.taskName,
                    listOf(execution.taskInstance.id),
                    listOf(execution.data),
                    listOf(execution.executionTime),
                    listOf(execution.isPicked),
                    listOf(execution.pickedBy),
                    listOf(execution.lastSuccess),
                    execution.lastFailure,
                    listOf(execution.consecutiveFailures),  // Modified here
                    null,
                    0
                )
            }

    }

    fun groupTasks(tasks: List<TaskModel>): List<TaskModel> {
        return tasks.stream().collect(
            Collectors.groupingBy { obj: TaskModel -> obj.taskName }
        ).values
            .map { taskModels: List<TaskModel> ->
                val taskModel = taskModels[0]
                taskModel.taskInstance = taskModels.stream()
                    .map { obj: TaskModel -> obj.taskInstance }
                    .flatMap { obj: List<String>? -> obj?.stream() }
                    .collect(Collectors.toList())
                taskModel.taskData = taskModels.stream()
                    .map { obj: TaskModel -> obj.taskData }
                    .flatMap { obj: List<Any>? -> obj?.stream() }
                    .collect(Collectors.toList())
                taskModel.executionTime = taskModels.stream()
                    .map { obj: TaskModel -> obj.executionTime }
                    .flatMap { obj: List<Instant>? -> obj?.stream() }
                    .collect(Collectors.toList())
                taskModel.isPicked = taskModels.stream()
                    .map { obj: TaskModel -> obj.isPicked }
                    .flatMap { obj: List<Boolean>? -> obj?.stream() }
                    .collect(Collectors.toList())
                taskModel.pickedBy = taskModels.stream()
                    .map { obj: TaskModel -> obj.pickedBy }
                    .flatMap { obj: List<String>? -> obj?.stream() }
                    .collect(Collectors.toList())
                taskModel.lastSuccess = taskModels.stream()
                    .map { obj: TaskModel -> obj.lastSuccess }
                    .flatMap { obj: List<Instant>? -> obj?.stream() }
                    .collect(Collectors.toList())
                taskModel.lastFailure = taskModels.stream()
                    .map { obj: TaskModel? -> obj?.lastFailure }
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null)
                taskModel.consecutiveFailures = taskModels.stream()
                    .map { obj: TaskModel -> obj.consecutiveFailures }
                    .flatMap { obj: List<Int>? -> obj?.stream() }
                    .collect(Collectors.toList()) // Modified here
                taskModel
            }
    }

    fun mapAllExecutionsToTaskModel(
        scheduledExecutions: List<ScheduledExecution<Any>>
    ): List<TaskModel> {
        return groupTasks(mapScheduledExecutionsToTaskModel(scheduledExecutions))
    }

    fun mapAllExecutionsToTaskModelUngrouped(
        scheduledExecutions: List<ScheduledExecution<Any>>
    ): List<TaskModel> {
        return mapScheduledExecutionsToTaskModel(scheduledExecutions)
    }
}