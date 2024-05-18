package ru.galtsev.coursework.e2e_framework.ui.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.math.ceil


class GetTasksResponse @JsonCreator constructor(
    @param:JsonProperty("numberOfItems") val numberOfItems: Int,
    @JsonProperty("items") pagedTasks: List<TaskModel>,
    @JsonProperty("pageSize") pageSize: Int
) {
    val numberOfPages: Int = if (numberOfItems == 0) 0 else ceil(numberOfItems.toDouble() / pageSize)
        .toInt()
    private val items: List<TaskModel> = pagedTasks

    fun getItems(): List<TaskModel> {
        return items
    }
}