package ru.galtsev.coursework.e2e_framework.entity

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*


@Entity
@Table(name = "test_cases_tags")
class TestCaseTag(

    @Id
    @Column(name = "tag_id")
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    var id: UUID? = null,


    @Column(name = "name")
    var name: String,


    @ManyToMany
    var testCaseContext: List<TestCaseContext>? = listOf()
) {
}