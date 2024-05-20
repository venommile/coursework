package ru.galtsev.coursework.e2e_framework.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import ru.galtsev.coursework.e2e_framework.entity.TestCaseContext
import java.util.*

interface TestCaseContextRepository : JpaRepository<TestCaseContext, UUID> {

    fun findTestCaseContextByTaskNameAndTaskId(taskName: String, taskId: String): Optional<TestCaseContext>


    fun findTestCaseByMethodName(testMethodName: String): Optional<TestCaseContext>
}