package ru.galtsev.coursework.e2e_framework.ui.service

import com.github.kagkarlsson.scheduler.ScheduledExecution
import com.github.kagkarlsson.scheduler.ScheduledExecutionsFilter
import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.task.TaskInstanceId
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap


@Component
class TaskCaching {
    private val taskStatusCache: MutableMap<String, String> = ConcurrentHashMap()
    private val taskDataCache: MutableList<ScheduledExecution<Any>> = ArrayList()

    fun getExecutionsFromCacheOrDB(
        isRefresh: Boolean, scheduler: Scheduler
    ): List<ScheduledExecution<Any>> {
        if (isRefresh || taskDataCache.isNotEmpty()) {
            val executions =
                getExecutionsFromDBWithoutUpdatingCache(scheduler)
            updateCache(executions)
            return executions
        } else {
            return taskDataCache
        }
    }

    fun getExecutionsFromDBWithoutUpdatingCache(
        scheduler: Scheduler
    ): List<ScheduledExecution<Any>> {
        val executions: MutableList<ScheduledExecution<Any>> = scheduler.getScheduledExecutions()
        executions.addAll(
            scheduler.getScheduledExecutions(ScheduledExecutionsFilter.all().withPicked(true))
        )
        return executions
    }

    fun updateCache(executions: List<ScheduledExecution<Any>>) {
        taskStatusCache.clear()
        taskDataCache.clear()
        taskDataCache.addAll(executions)
        for (execution in executions) {
            taskStatusCache[getUniqueId(execution)] = getStatus(execution)
        }
    }

    fun getUniqueId(task: ScheduledExecution<Any>): String {
        return task.taskInstance.taskName + "_" + task.taskInstance.id
    }

    fun getStatus(task: ScheduledExecution<Any>): String {
        return ((if (task.consecutiveFailures > 0) "1" else "0")
                + (if (task.pickedBy != null) "1" else "0"))
    }

    fun getStatusFromCache(taskInstance: TaskInstanceId): String? {
        val uniqueId = taskInstance.taskName + "_" + taskInstance.id
        return taskStatusCache[uniqueId]
    }


}