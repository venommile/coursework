package ru.galtsev.coursework.e2e_framework.entity

import jakarta.persistence.*
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import org.hibernate.annotations.UuidGenerator
import java.util.*


@Entity
@Table(name = "test_cases_history")
class TestCaseContext(


    @Id
    @Column(name = "test_context_id")
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    var id: UUID? = null,


    @Column(name = "task_id")
    var taskId: String? = "",


    @Column(name = "task_name")
    var taskName: String? = "",


    @Column(name = "project_name")
    var projectName: String? = "",

    @Column(name = "description")
    var description: String,


    @Column(name = "method_name")
    var methodName: String,

    @ManyToMany(fetch = FetchType.EAGER)
    var testCaseTags: List<TestCaseTag> = listOf(),

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderBy("executionTime DESC")
    @Cascade(CascadeType.ALL)
    var history: MutableList<TestCaseHistory> = mutableListOf(),

    )