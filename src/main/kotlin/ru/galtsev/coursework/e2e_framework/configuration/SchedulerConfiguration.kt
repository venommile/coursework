package ru.galtsev.coursework.e2e_framework.configuration

import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.TaskRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible


@Configuration
class SchedulerConfiguration {


    @Bean
    fun schedulerTaskRepository(scheduler: Scheduler): TaskRepository {
        return readInstanceProperty(scheduler, "schedulerTaskRepository")
    }

    private fun readInstanceProperty(scheduler: Scheduler, propertyName: String): TaskRepository {

        return (scheduler::class.members
            .first { it.name == propertyName }
            .apply { isAccessible = true } as KProperty1<Any, Scheduler>).get(scheduler) as TaskRepository
    }
}