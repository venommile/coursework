package ru.galtsev.coursework.e2e_framework.entity

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.util.*


//@Entity
@Table(name = "test_cases_history")
class TestCaseContext(


    @Id
    @Column(name = "test_context_id")
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    var id: UUID,


    @Column(name = "task_id")
    var taskId: String,


    @Column(name = "task_name")
    var taskName: String,


    @Column(name = "description")
    var description: String,


    @ManyToOne
    var tags: List<Tag>

)