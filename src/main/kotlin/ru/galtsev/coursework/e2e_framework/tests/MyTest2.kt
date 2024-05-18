package ru.galtsev.coursework.e2e_framework.tests

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.galtsev.coursework.e2e_framework.infra.annotation.TestAn


@TestAn(projectName = "kek")
class MyTest2 {


    @Test
    @DisplayName("моё супер-имя")
    fun test(){

        println("inside test4")
        true shouldBe false
    }


    @Test
    @DisplayName("kek2")
    fun test2(){
        println("inside test3")

        true shouldBe true

    }
}