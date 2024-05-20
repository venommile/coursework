package ru.galtsev.coursework.e2e_framework.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.galtsev.coursework.e2e_framework.entity.TestCaseHistory
import java.util.*

interface TestCaseHistoryRepository : JpaRepository<TestCaseHistory, UUID> {


}