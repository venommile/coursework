package ru.galtsev.coursework.e2e_framework.tests

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import ru.galtsev.coursework.e2e_framework.infra.annotation.TestAn
import java.util.concurrent.TimeUnit


@TestAn(projectName = "kek", timeUnit = TimeUnit.SECONDS, delay = 1000)
class MyTest {


    @Test
    @Tags(
        value = [
            Tag("hello"),


            Tag("world"),
        ]
    )
    fun test() {

        println("inside test")
        true shouldBe false
    }


    @Test
    @DisplayName("kek2")
    fun test2() {
        println("inside test2")

        true shouldBe true

    }
}