package ru.galtsev.coursework.e2e_framework.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.galtsev.coursework.e2e_framework.entity.TestCaseTag
import java.util.*

interface TagRepository : JpaRepository<TestCaseTag, UUID> {


    fun findTagByNameIgnoreCase(name: String): Optional<TestCaseTag>
}