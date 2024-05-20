package ru.galtsev.coursework.e2e_framework.tests

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.galtsev.coursework.e2e_framework.infra.annotation.TestAn
import ru.galtsev.coursework.e2e_framework.infra.config.MetricTestWatcher


@TestAn(projectName = "kek")
@ExtendWith(MetricTestWatcher::class)
class MyTest2 {


    @Test
    @DisplayName("моё супер-имя")
    fun test(){

        println("inside test4")
        true shouldBe false
    }


    @Test
    @DisplayName("kekwerw2")
    fun test2(){
        println("inside test3")

        true shouldBe true

    }


}