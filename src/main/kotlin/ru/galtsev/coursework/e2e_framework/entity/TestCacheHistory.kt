package ru.galtsev.coursework.e2e_framework.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.util.*

//@Entity
@Table(name = "test_case_history")
class TestCaseHistory(
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    var id: UUID,

    ) {
}