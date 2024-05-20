package ru.galtsev.coursework.e2e_framework.infra.config

import com.github.kagkarlsson.scheduler.task.helper.RecurringTaskWithPersistentSchedule
import ru.galtsev.coursework.e2e_framework.infra.config.TestSchedulerConfiguration.ScheduleAndNoData
import java.lang.reflect.Method

data class ScheduledTask(
    val schedule: RecurringTaskWithPersistentSchedule<ScheduleAndNoData>,
    val method: Method,
    val projectName: String,
    val description: String,

) {
}