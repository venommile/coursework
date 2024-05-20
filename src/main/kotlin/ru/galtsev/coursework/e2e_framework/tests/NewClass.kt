package ru.galtsev.coursework.e2e_framework.tests

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.galtsev.coursework.e2e_framework.infra.annotation.TestAn
import ru.galtsev.coursework.e2e_framework.infra.config.MetricTestWatcher
import java.util.concurrent.TimeUnit


@TestAn(projectName = "google-tests", timeUnit = TimeUnit.DAYS, delay = 1)
@ExtendWith(MetricTestWatcher::class)

class GoogleTests {
    @BeforeEach
    fun setUp() {
        Configuration.browser = "chrome"
        Configuration.headless = true // Отключить GUI для тестов
    }

    @AfterEach
    fun tearDown() {
        Selenide.closeWebDriver()
    }

    @Test
    @DisplayName("Открытие страницы гугла")
    fun testOpenGoogleHomePage() {
        Selenide.open("https://www.google.com")
        val pageTitle = Selenide.title()
        // Проверяем, что заголовок страницы "Google"
        Assertions.assertThat(pageTitle).isEqualTo("Google")
    }

    @Test
    @DisplayName("Поиск информации о Junit в Google")
    @Tags(
        value = [Tag("search")]
    )
    fun testSearch() {
        Selenide.open("https://www.google.com")
        Selenide.`$`("[name='q']").setValue("JUnit 5").pressEnter()
        val pageTitle = Selenide.title()
        // Проверяем, что заголовок страницы содержит "JUnit 5 - Поиск в Google"
        Assertions.assertThat(pageTitle).contains("JUnit 5 - Поиск в Google")
    }

    @Test
    @DisplayName("проверка результата выдачи")
    fun testResultsTitle() {
        Selenide.open("https://www.google.com")
        Selenide.`$`("[name='q']").setValue("JUnit 5").pressEnter()
        val pageTitle = Selenide.title()
        // Проверка, что заголовок страницы содержит "JUnit 5"
        Assertions.assertThat(pageTitle).contains("JUnit 5")
    }
}
