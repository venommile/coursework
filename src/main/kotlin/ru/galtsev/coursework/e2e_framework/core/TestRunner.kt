package ru.galtsev.coursework.e2e_framework.core

import org.junit.platform.engine.DiscoverySelector
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

class TestRunner(
    private val projectName: String,
    private val method: Method,

) : Runnable {

    private val log = LoggerFactory.getLogger(javaClass)


    private val filters = listOf(".*Tests", ".*Test", ".*IT", ".*it")


    private fun runAllTestsInClass() {
        val selectors = mutableListOf<DiscoverySelector>()

        selectors.add(
            DiscoverySelectors.selectMethod(method.declaringClass, method)
        )


        val request = LauncherDiscoveryRequestBuilder.request()
            .selectors(selectors)
            .build()

        val launcher = LauncherFactory.create()
        val testPlan = launcher.discover(request)

        launcher.execute(testPlan)
    }

    override fun run() {
        log.info("Start running tests...")
        runAllTestsInClass()
        log.info("finished running tests...")
    }


}