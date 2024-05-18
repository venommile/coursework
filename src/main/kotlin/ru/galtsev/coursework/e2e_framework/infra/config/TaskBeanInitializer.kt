package ru.galtsev.coursework.e2e_framework.infra.config

import com.github.kagkarlsson.scheduler.task.ExecutionContext
import com.github.kagkarlsson.scheduler.task.TaskInstance
import com.github.kagkarlsson.scheduler.task.helper.RecurringTaskWithPersistentSchedule
import com.github.kagkarlsson.scheduler.task.helper.Tasks
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.annotation.AnnotationUtils
import ru.galtsev.coursework.e2e_framework.core.TestRunner
import ru.galtsev.coursework.e2e_framework.infra.annotation.TestAn
import ru.galtsev.coursework.e2e_framework.infra.config.TestSchedulerConfiguration.ScheduleAndNoData
import java.lang.reflect.Method
import java.util.function.Supplier

abstract class TaskBeanInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(applicationContext: GenericApplicationContext) {
        CONTEXT_THREAD_LOCAL.set(applicationContext)
        initialize()
        CONTEXT_THREAD_LOCAL.remove()
    }

    protected abstract fun initialize()


    protected fun task(projectName: String, testSuites: Set<Class<*>>) {
        val context = CONTEXT_THREAD_LOCAL.get()


        val methodMutableList = getMethods(testSuites)



        for (method in methodMutableList) {
            val methodName = method.declaringClass.name + "#" + method.name
            var determinedProjectName = AnnotationUtils.findAnnotation(method, TestAn::class.java)
                ?.projectName

            if (determinedProjectName == null) {
                determinedProjectName = projectName
            }

            var name = AnnotationUtils.findAnnotation(method, DisplayName::class.java)?.value
            if (name?.isBlank() != false) {
                name = methodName
            } else {
                name += "|||||" + methodName.hashCode()
            }


            val task = Tasks.recurringWithPersistentSchedule(
                name, ScheduleAndNoData::class.java
            ).execute { _: TaskInstance<ScheduleAndNoData>, _: ExecutionContext? ->
                TestRunner(methodName, method).run()
            }
            context.registerBean(
                methodName,
                RecurringTaskWithPersistentSchedule::class.java,
                Supplier {
                    task
                })

            context.registerBean(
                methodName + "scheduledTask",
                ScheduledTask::class.java,
                Supplier {
                    ScheduledTask(
                        schedule = context.getBean(
                            methodName,
                            RecurringTaskWithPersistentSchedule::class.java
                        ) as RecurringTaskWithPersistentSchedule<ScheduleAndNoData>,
                        method = method,
                        projectName = determinedProjectName
                    )
                }

            )
        }


    }


    private fun getMethods(testSuites: Set<Class<*>>): MutableList<Method> {
        val methodMutableList = mutableListOf<Method>()
        for (testSuite in testSuites) {
            for (method in testSuite.methods) {
                val annotation = AnnotationUtils.getAnnotation(method, Test::class.java)
                if (annotation != null) {
                    methodMutableList += method
                }
            }
        }
        return methodMutableList
    }

    companion object {
        private val CONTEXT_THREAD_LOCAL = ThreadLocal<GenericApplicationContext>()
    }
}
