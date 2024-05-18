package ru.galtsev.coursework.e2e_framework.entity

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.util.*


//@Entity
@Table(name = "test_cases_tags")
class Tag(

    @Id
    @Column(name = "tag_id")
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    var id: UUID,


    @Column(name = "name")
    var name: String,


    @OneToMany
    var testCaseContext: TestCaseContext
) {
}