package ru.galtsev.coursework.e2e_framework.tests

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.galtsev.coursework.e2e_framework.infra.annotation.TestAn
import ru.galtsev.coursework.e2e_framework.infra.config.MetricTestWatcher
import java.util.concurrent.TimeUnit


@TestAn(projectName = "kek", timeUnit = TimeUnit.MINUTES, delay = 1)
@ExtendWith(MetricTestWatcher::class)
class MyTest {


    @Test
    @Tags(
        value = [
            Tag("hello"),


            Tag("world"),
        ]
    )
    @DisplayName("some-name")
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