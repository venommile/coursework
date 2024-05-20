package ru.galtsev.coursework.e2e_framework.infra.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import ru.galtsev.coursework.e2e_framework.repository.TestCaseContextRepository
import ru.galtsev.coursework.e2e_framework.repository.TestCaseHistoryRepository


@Configuration
class MetricTestWatcherConfig(

    val testCaseHistoryRepository: TestCaseHistoryRepository,

    val testCaseContextRepository: TestCaseContextRepository
) {


    @PostConstruct
    fun init() {
        MetricTestWatcher.testCaseHistoryRepository = testCaseHistoryRepository
        MetricTestWatcher.testCaseContextRepository = testCaseContextRepository

    }
}
