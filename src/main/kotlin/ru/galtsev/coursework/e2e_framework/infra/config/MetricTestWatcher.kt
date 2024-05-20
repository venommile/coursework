package ru.galtsev.coursework.e2e_framework.infra.config

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestWatcher
import ru.galtsev.coursework.e2e_framework.entity.ErrorType
import ru.galtsev.coursework.e2e_framework.entity.TestCaseHistory
import ru.galtsev.coursework.e2e_framework.repository.TestCaseContextRepository
import ru.galtsev.coursework.e2e_framework.repository.TestCaseHistoryRepository
import java.util.*


object MetricTestWatcher : TestWatcher {

    lateinit var testCaseHistoryRepository: TestCaseHistoryRepository

    lateinit var testCaseContextRepository: TestCaseContextRepository


    override fun testFailed(context: ExtensionContext, cause: Throwable) {


        if (context.testMethod.isPresent) {
            val method = context.testMethod.get()

            val testCase = testCaseContextRepository.findTestCaseByMethodName(method.toString())
            val testCaseContext = testCase.get()
            val message = cause.message ?: ""
            val history = testCaseHistoryRepository.save(
                TestCaseHistory(
                    success = false,
                    errorType = when (cause) {
                        is AssertionError -> ErrorType.ASSERTIONS_ERROR
                        else -> ErrorType.BROKEN_ERROR
                    },
                    errorMessage = if (message.length > 250) message.substring(0, 250) else message,
                    testCaseContext = testCaseContext
                )
            )
            testCaseContext.history += history
            testCaseContextRepository.save(testCaseContext)
        }
        super.testFailed(context, cause)
    }

    override fun testSuccessful(context: ExtensionContext) {
        if (context.testMethod.isPresent) {
            val method = context.testMethod.get()

            val testCase = testCaseContextRepository.findTestCaseByMethodName(method.toString()).get()
           val history =  testCaseHistoryRepository.save(
                TestCaseHistory(
                    success = true,
                    errorMessage = "success",
                    testCaseContext = testCase
                )
            )

            testCase.history += history
            testCaseContextRepository.save(testCase)


        }

        super.testSuccessful(context)
    }

    override fun testDisabled(context: ExtensionContext, reason: Optional<String>) {
        super.testDisabled(context, reason)
    }
}