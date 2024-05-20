package ru.galtsev.coursework.e2e_framework.infra.config

import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.task.helper.ScheduleAndData
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay
import io.github.classgraph.ClassGraph
import org.junit.jupiter.api.Tags
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.jdbc.core.JdbcTemplate
import ru.galtsev.coursework.e2e_framework.entity.TestCaseContext
import ru.galtsev.coursework.e2e_framework.entity.TestCaseTag
import ru.galtsev.coursework.e2e_framework.infra.annotation.TestAn
import ru.galtsev.coursework.e2e_framework.repository.TagRepository
import ru.galtsev.coursework.e2e_framework.repository.TestCaseContextRepository
import java.io.Serializable
import java.util.concurrent.TimeUnit


@Configuration
class TestSchedulerConfiguration(
    private val scheduler: Scheduler,
    private val tagRepository: TagRepository,
    private val testCaseContextRepository: TestCaseContextRepository,
    private val tasks: MutableList<ScheduledTask>,
    private val jdbcTemplate: JdbcTemplate
) {

    private val log = LoggerFactory.getLogger(TestSchedulerConfiguration::class.java)




    @EventListener(ContextRefreshedEvent::class)
    fun configureTasks() {

        val tests = findAllTests("ru.galtsev.coursework.e2e_framework.tests")

        if (tests.isEmpty()) {
            throw RuntimeException("не найдены тесты")
        }

        val projects = splitTestsByProject(tests)
        log.debug("projects: ${projects.size}")
        log.info("Tests list $tests")
        log.info("Projects list $projects")


        jdbcTemplate.execute("Delete from scheduled_tasks")

        tasks.forEach { scheduledTask ->


            val resultTags = mutableListOf<String>()
            val classTags = AnnotationUtils.findAnnotation(scheduledTask.method.declaringClass, Tags::class.java)


            classTags?.value?.map { it.value }?.let { resultTags.addAll(it) }


            val tags = AnnotationUtils.findAnnotation(scheduledTask.method, Tags::class.java)
            tags?.value?.map { it.value }?.let { resultTags.addAll(it) }


            var projectName: String = ""
            val testAnnotation = AnnotationUtils.findAnnotation(scheduledTask.method, TestAn::class.java)

            projectName = if (testAnnotation?.projectName?.isNotBlank() == true) {
                val resultAn =
                    AnnotationUtils.findAnnotation(scheduledTask.method.declaringClass, TestAn::class.java)

                if (resultAn?.projectName?.isNotBlank() == true) {
                    resultAn.projectName
                } else {
                    throw RuntimeException("необходимо задать projectName")
                }
            } else {
                scheduledTask.projectName
            }


            val testCaseContext = testCaseContextRepository.findTestCaseContextByTaskNameAndTaskId(
                scheduledTask.description,
                scheduledTask.method.toString()
            )


            println(testCaseContext.isEmpty)
            val tagsFromDb = resultTags.map {
                testCaseTag(it)
            }

            if (testCaseContext.isEmpty) {
                testCaseContextRepository.save(
                    TestCaseContext(
                        taskId = scheduledTask.method.toString(),
                        taskName = scheduledTask.description,
                        projectName = projectName,
                        description = "",
                        testCaseTags = tagsFromDb,
                        methodName = scheduledTask.method.toString(),
                    )
                )
            }




            val (timeUnit: TimeUnit, value: Int) = getDelayValue(testAnnotation, scheduledTask)



            scheduler.schedule(
                scheduledTask.schedule.schedulableInstance(
                    scheduledTask.method.toString(),
                    ScheduleAndNoData(
                        scheduledTask.projectName,
                        tags = resultTags,
                        timeUnit = timeUnit,
                        delayValue = value
                    )
                )
            )
        }
    }

    private fun testCaseTag(it: String): TestCaseTag {
        val tagByNameIgnoreCase = tagRepository.findTagByNameIgnoreCase(it)
        if (tagByNameIgnoreCase.isEmpty) {
            return tagRepository.save(
                TestCaseTag(
                    name = it
                )
            )
        }
        return tagByNameIgnoreCase.get()
    }

    private fun getDelayValue(
        fixedDelayOnMethod: TestAn?,
        scheduledTask: ScheduledTask
    ): Pair<TimeUnit, Int> {
        var fixedDelayOnMethod1 = fixedDelayOnMethod
        val timeUnit: TimeUnit
        val value: Int

        if (isNotSet(fixedDelayOnMethod1)) {
            fixedDelayOnMethod1 =
                AnnotationUtils.findAnnotation(scheduledTask.method.declaringClass, TestAn::class.java)

            if (isNotSet(fixedDelayOnMethod1)) {

                timeUnit = TimeUnit.DAYS
                value = 1
            } else {
                timeUnit = fixedDelayOnMethod1?.timeUnit ?: TimeUnit.DAYS
                value = fixedDelayOnMethod1?.delay ?: 1
            }
        } else {
            timeUnit = fixedDelayOnMethod1?.timeUnit ?: TimeUnit.DAYS
            value = fixedDelayOnMethod1?.delay ?: 1
        }
        return Pair(timeUnit, value)
    }

    private fun isNotSet(fixedDelayOnMethod: TestAn?) =
        fixedDelayOnMethod == null || fixedDelayOnMethod.timeUnit == TimeUnit.NANOSECONDS


    fun findAllTests(basePackage: String): Set<Class<*>> {
        val resultSet = mutableSetOf<Class<*>>()
        ClassGraph()
            .acceptPackages(basePackage)
            .enableAnnotationInfo()
            .scan()
            .use {
                for (routeClassInfo in it.getClassesWithAnnotation(TestAn::class.qualifiedName)) {
                    resultSet.add(routeClassInfo.loadClass())
                }
            }
        return resultSet
    }

    fun splitTestsByProject(allTests: Set<Class<*>>): Map<String, Set<Class<*>>> {
        val map = mutableMapOf<String, MutableSet<Class<*>>>()
        allTests.forEach {
            val projectAnnotation = it.getAnnotation(TestAn::class.java)
            val projectName = projectAnnotation?.projectName
            if (projectName?.isNotBlank() == true) {
                var result = map[projectName]
                if (result == null) {
                    result = mutableSetOf()
                }
                result.add(it)
                map[projectName] = result
            }
        }
        return map
    }


    data class ScheduleAndNoData(

        private val projectName: String,
        private val tags: List<String>,
        private val timeUnit: TimeUnit,
        private val delayValue: Int
    ) : ScheduleAndData, Serializable {


        override fun getSchedule(): FixedDelay {

            return when (timeUnit) {
                TimeUnit.DAYS -> FixedDelay.ofHours(24 * delayValue)
                TimeUnit.HOURS -> FixedDelay.ofHours(delayValue)
                TimeUnit.MINUTES -> FixedDelay.ofMinutes(delayValue)
                TimeUnit.SECONDS -> FixedDelay.ofSeconds(delayValue)
                else -> FixedDelay.ofSeconds(10)
            }
        }

        override fun getData(): Any {
            return mapOf(
                "projectName" to projectName,
                "tags" to tags
            )
        }
    }

}