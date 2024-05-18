package ru.galtsev.coursework.e2e_framework

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.scheduling.annotation.EnableScheduling
import ru.galtsev.coursework.e2e_framework.infra.config.TaskInitializer

@SpringBootApplication
@EnableScheduling
class CourseworkE2eFrameworkApplication


fun main(args: Array<String>) {
    SpringApplicationBuilder()
        .sources(CourseworkE2eFrameworkApplication::class.java)
        .initializers(TaskInitializer())
        .run(*args)
}
