package ru.galtsev.coursework.e2e_framework.ui.mapper

import com.github.kagkarlsson.scheduler.ScheduledExecution
import ru.galtsev.coursework.e2e_framework.ui.dto.TaskModel
import ru.galtsev.coursework.e2e_framework.ui.dto.TaskRequestParams.TaskFilter
import ru.galtsev.coursework.e2e_framework.ui.dto.TaskRequestParams.TaskSort
import java.time.Instant
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.math.min

object QueryUtils {
    fun <T> paginate(allItems: List<T>, pageNumber: Int, pageSize: Int): List<T> {
        val startIndex = pageNumber * pageSize
        val endIndex = min((startIndex + pageSize).toDouble(), allItems.size.toDouble()).toInt()

        return if ((startIndex < endIndex)) allItems.subList(startIndex, endIndex) else ArrayList()
    }

    fun filterTasks(
        tasks: List<TaskModel>, filter: TaskFilter?
    ): List<TaskModel> {
        return tasks.stream()
            .filter { task: TaskModel ->
                when (filter) {
                    TaskFilter.FAILED -> Objects.requireNonNull<List<Int>?>(task.consecutiveFailures).stream()
                        .anyMatch { failures: Int -> failures != 0 }

                    TaskFilter.RUNNING -> Objects.requireNonNull<List<Boolean>?>(task.isPicked).stream()
                        .anyMatch { obj: Boolean -> obj }

                    TaskFilter.SCHEDULED -> IntStream.range(
                        0,
                        Objects.requireNonNull<List<Boolean>?>(task.isPicked).size
                    )
                        .anyMatch { i: Int -> !Objects.requireNonNull<List<Boolean>?>(task.isPicked)[i] && task.consecutiveFailures!![i] == 0 }

                    else -> true
                }
            }
            .collect(Collectors.toList<TaskModel>())
    }

    fun filterExecutions(
        executions: List<ScheduledExecution<Any>>, filter: TaskFilter?, taskName: String?
    ): List<ScheduledExecution<Any>> {
        return executions.stream()
            .filter { execution: ScheduledExecution<Any> ->
                if (taskName != null && taskName != execution.taskInstance.taskName) {
                    return@filter false
                }
                when (filter) {
                    TaskFilter.FAILED -> return@filter execution.consecutiveFailures != 0
                    TaskFilter.RUNNING -> return@filter execution.isPicked
                    TaskFilter.SCHEDULED -> return@filter execution.consecutiveFailures == 0 && !execution.isPicked
                    else -> return@filter true
                }
            }
            .collect(Collectors.toList())
    }

    fun sortTasks(tasks: List<TaskModel>, sortType: TaskSort, isAsc: Boolean): List<TaskModel> {

        val comparator =
            Comparator.comparing(
                { task: TaskModel ->
                    task.executionTime!!.stream()
                        .min { obj: Instant, otherInstant: Instant? -> obj.compareTo(otherInstant) }
                        .orElse(Instant.MAX)
                },
                Comparator.nullsLast { obj: Instant, otherInstant: Instant? -> obj.compareTo(otherInstant) })
        return tasks.stream().sorted(comparator).toList()

    }

    fun searchByTaskName(
        tasks: List<TaskModel>, searchTermTaskName: String?, isExactMatch: Boolean
    ): List<TaskModel> {
        return search(tasks, searchTermTaskName, true, isExactMatch)
    }

    fun searchByTaskInstance(
        tasks: List<TaskModel>, searchTermTaskInstance: String?, isExactMatch: Boolean
    ): List<TaskModel> {
        return search(tasks, searchTermTaskInstance, false, isExactMatch)
    }

    fun search(
        tasks: List<TaskModel>, searchTerm: String?, isTaskNameSearch: Boolean, isExactMatch: Boolean
    ): List<TaskModel> {
        if (searchTerm == null || searchTerm.trim { it <= ' ' }.isEmpty()) {
            return tasks
        }

        return tasks.stream()
            .filter { task: TaskModel ->
                val lowerCaseTerm = searchTerm.lowercase(Locale.getDefault())
                val isTermPresent = if (isTaskNameSearch) {
                    if (isExactMatch
                    ) task.taskName.equals(lowerCaseTerm, ignoreCase = true)
                    else task.taskName!!.lowercase(Locale.getDefault()).contains(lowerCaseTerm)
                } else {
                    task.taskInstance!!.stream()
                        .anyMatch { instance: String? ->
                            (instance != null
                                    && (if (isExactMatch
                            ) instance.equals(lowerCaseTerm, ignoreCase = true)
                            else instance.lowercase(Locale.getDefault()).contains(lowerCaseTerm)))
                        }
                }
                isTermPresent
            }
            .collect(Collectors.toList())
    }

    fun logSearchCondition(
        searchTerm: String, params: MutableMap<String?, Any?>, isTaskName: Boolean, isExactMatch: Boolean
    ): String {
        val conditions = StringBuilder()
        val termConditions: MutableList<String> = ArrayList()
        val termKey = "searchTerm" + (if (isTaskName) "TaskName" else "TaskInstance")
        params[termKey] = if (isExactMatch) searchTerm else "%$searchTerm%"
        val condition =
            if (isTaskName
            ) "LOWER(task_name) " + (if (isExactMatch) "=" else "LIKE") + " LOWER(:" + termKey + ")"
            else "LOWER(task_instance) " + (if (isExactMatch) "=" else "LIKE") + " LOWER(:" + termKey + ")"
        termConditions.add(condition)

        return conditions.append(java.lang.String.join(" AND ", termConditions)).toString()
    }
}