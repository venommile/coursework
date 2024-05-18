package ru.galtsev.coursework.e2e_framework.infra.annotation

import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit


@Test
annotation class TestAn(
    val projectName: String,
    val timeUnit: TimeUnit = TimeUnit.NANOSECONDS,
    val delay: Int = -1
) {


}