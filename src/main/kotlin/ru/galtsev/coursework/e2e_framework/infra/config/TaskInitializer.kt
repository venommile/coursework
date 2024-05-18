package ru.galtsev.coursework.e2e_framework.infra.config

import io.github.classgraph.ClassGraph
import ru.galtsev.coursework.e2e_framework.infra.annotation.TestAn

class TaskInitializer : TaskBeanInitializer() {
    override fun initialize() {
        val tests = findAllTests("ru.galtsev")

        if (tests.isEmpty()) {
            throw RuntimeException("не найдены тесты")
        }

        val projects = splitTestsByProject(tests)

        projects.forEach { task(it.key, it.value) }

    }

    fun findAllTests(basePackage: String): Set<Class<*>> {
        val resultSet = mutableSetOf<Class<*>>()
        ClassGraph()
            .acceptPackages(basePackage)
            .enableAnnotationInfo()
            .scan()
            .use {
                for (routeClassInfo in it.getClassesWithAnnotation(TestAn::class.qualifiedName)) {
                    resultSet.add(routeClassInfo.loadClass())
                }
            }
        return resultSet
    }

    fun splitTestsByProject(allTests: Set<Class<*>>): Map<String, Set<Class<*>>> {
        val map = mutableMapOf<String, MutableSet<Class<*>>>()
        allTests.forEach {
            val projectAnnotation = it.getAnnotation(TestAn::class.java)
            val projectName = projectAnnotation?.projectName
            if (projectName?.isNotBlank() == true) {
                var result = map[projectName]
                if (result == null) {
                    result = mutableSetOf()
                }
                result.add(it)
                map[projectName] = result
            }
        }
        return map
    }
}