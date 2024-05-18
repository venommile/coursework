package ru.galtsev.coursework.e2e_framework.ui.dto

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Instant
import java.util.stream.Collectors


class TaskModel(
    var taskName: String? = null,
    var taskInstance: List<String>? = null,
    var taskData: List<Any>? = null,
    var executionTime: List<Instant>? = null,
    var isPicked: List<Boolean>? = null,
    var pickedBy: List<String>? = null,
    var lastSuccess: List<Instant>? = null,
    var lastFailure: Instant? = null,
    var consecutiveFailures: List<Int>? = null,
    var lastHeartbeat: Instant? = null,
    var version: Int = 0

) {


    private fun serializeTaskData(inputTaskDataList: List<Any>): List<Any> {
        return inputTaskDataList.stream()
            .map { data: Any? ->
                try {
                    val serializedData = objectMapper.writeValueAsString(data)
                    return@map objectMapper.readValue<Any>(
                        serializedData,
                        Any::class.java
                    )
                } catch (e: JsonProcessingException) {
                    throw RuntimeException(e)
                }
            }
            .collect(Collectors.toList())
    }


    companion object {
        private val objectMapper = ObjectMapper()
    }
}