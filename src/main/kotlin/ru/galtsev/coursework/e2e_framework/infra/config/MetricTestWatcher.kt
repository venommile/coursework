package ru.galtsev.coursework.e2e_framework.infra.config

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestWatcher
import org.springframework.stereotype.Service
import java.util.*


@Service
class MetricTestWatcher : TestWatcher {


    override fun testFailed(context: ExtensionContext?, cause: Throwable?) {


        super.testFailed(context, cause)
    }

    override fun testSuccessful(context: ExtensionContext?) {
        super.testSuccessful(context)
    }

    override fun testDisabled(context: ExtensionContext?, reason: Optional<String>?) {
        super.testDisabled(context, reason)
    }
}