package ru.galtsev.coursework.e2e_framework.entity

import jakarta.persistence.*
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UuidGenerator
import java.time.Instant
import java.util.*

@Entity
@Table(name = "test_case_history")
class TestCaseHistory(
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    var id: UUID? = null,


    @Column(name = "success")
    var success: Boolean,


    @Column(name = "error_message")
    var errorMessage: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "error_type")
    var errorType: ErrorType? = null,

    @CreationTimestamp
    @Column(name = "execution_time")
    var executionTime: Instant = Instant.now(),


    @ManyToOne
    val testCaseContext: TestCaseContext
    ) {
}