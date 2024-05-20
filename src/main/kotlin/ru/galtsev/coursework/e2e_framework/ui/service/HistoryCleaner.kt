package ru.galtsev.coursework.e2e_framework.ui.service

import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.galtsev.coursework.e2e_framework.repository.TestCaseContextRepository
import ru.galtsev.coursework.e2e_framework.repository.TestCaseHistoryRepository
import java.util.concurrent.TimeUnit


@Service
class HistoryCleaner(
    private val testCaseHistoryRepository: TestCaseHistoryRepository,
    private val testCaseContextRepository: TestCaseContextRepository
) {


    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    @Transactional
    fun clear() {

        val testCaseContexts = testCaseContextRepository.findAll()

        for (testCaseContext in testCaseContexts) {
            val resultList = testCaseContext.history.drop(3)



            testCaseHistoryRepository.deleteAll(resultList)

        }

    }
}
