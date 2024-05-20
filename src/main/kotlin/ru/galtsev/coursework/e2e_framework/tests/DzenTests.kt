package ru.galtsev.coursework.e2e_framework.tests

import com.codeborne.selenide.Condition.visible
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide.*
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.openqa.selenium.chrome.ChromeOptions
import ru.galtsev.coursework.e2e_framework.infra.annotation.TestAn
import java.net.MalformedURLException
import java.util.concurrent.TimeUnit


@TestAn(projectName = "Yandex-tests", timeUnit = TimeUnit.DAYS, delay = 1)
class DzenTests {
    @BeforeEach
    @Throws(MalformedURLException::class)
    fun setUp() {
        // Устанавливаем использование удаленного WebDriver
        Configuration.remote = "http://158.160.167.135:4444//wd/hub"

        // Опции Chrome для запуска headless-режиме
        val options = ChromeOptions()

        Configuration.browserCapabilities = options
        Configuration.timeout = 10000
    }
    @AfterEach
    fun tearDown() {
        // Закрытие браузера после каждого теста
        closeWebDriver()
    }

    @Test
    @DisplayName("Проверяем, что заголовок страницы содержит \"Дзен\"")
    fun `main page title should contain "Дзен"`() {
        // Открываем сайт
        open("https://dzen.ru/")

        // Проверяем, что заголовок страницы содержит "Дзен"
        title() shouldContain "Дзен"
    }

    @Test
    @DisplayName(" Проверяем, что на странице есть поле поиска")
    fun `should have search input on main page`() {
        // Открываем сайт
        open("https://dzen.ru/")

        // Проверяем, что на странице есть поле поиска
        val searchInput = `$$`("[placeholder='Поиск']")
        searchInput shouldHaveSize 1
    }

    @Test
    @DisplayName("Проверяем, что новая страница открылась и содержит текст статьи")
    fun `should open an article from the main page`() {
        // Открываем сайт
        open("https://dzen.ru/")

        // Кликаем на первую статью в списке
        `$`("article").click()

        // Проверяем, что новая страница открылась и содержит текст статьи
        `$`("article").shouldBe(visible)
    }

}
