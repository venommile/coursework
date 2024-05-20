package ru.galtsev.coursework.e2e_framework.ui.controller

import jakarta.transaction.Transactional
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.galtsev.coursework.e2e_framework.repository.TagRepository
import ru.galtsev.coursework.e2e_framework.repository.TestCaseContextRepository
import ru.galtsev.coursework.e2e_framework.ui.dto.TaskRequestParams
import ru.galtsev.coursework.e2e_framework.ui.service.TaskLogic
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    .withZone(ZoneId.systemDefault())
@CrossOrigin
@Controller
class StartController(
    private val tagRepository: TagRepository,
    private val taskLogic: TaskLogic,
    private val testCaseContextRepository: TestCaseContextRepository
) {


    @GetMapping("/")
    @Transactional
    fun index(
        model: Model,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?,
        @RequestParam("tag") tag: String?,
        @RequestParam("projectName") projectName: String?,
        @RequestParam("taskName") taskName: String?,
    ): String {

        val resultSize = size ?: 5
        val resultPageIndex = if (page == null || page < 0) {
            0
        } else {
            page
        }

        val params = TaskRequestParams(
            refresh = true,
            filter = TaskRequestParams.TaskFilter.ALL,
            pageNumber = resultPageIndex,
            size = resultSize,
            sorting = TaskRequestParams.TaskSort.DEFAULT,
            searchTermTaskName = taskName ?: "",
            searchTermTaskInstance = "",
            asc = true,
            endTime = Instant.MAX,
            startTime = Instant.MIN,
            taskNameExactMatch = false,
            taskInstanceExactMatch = false
        )
        val result = taskLogic.getAllTasks(params)


        val tasks = mutableListOf<Task>()
        for (task in result.getItems()) {


            val status = if (task.executionTime!![0].isBefore(
                    LocalDateTime.of(2060, 1, 1, 1, 1, 1)
                        .toInstant(ZoneOffset.UTC)
                )
            ) "Scheduled" else "Muted"

            val name = task.taskName!!

            val id = task.taskInstance!![0]
            val nextExecutionTime = task.executionTime!![0]
            val context = testCaseContextRepository.findTestCaseContextByTaskNameAndTaskId(name, id).orElseThrow()
            val tags = context.testCaseTags.map { it.name }



            if ((tag.isNullOrBlank() || tag.lowercase() == "all" || tag in tags) && (projectName == null || context.projectName?.contains(
                    projectName
                ) == true)
            ) {


                tasks += listOf(
                    Task(
                        status = status,
                        name = name,
                        id = id,
                        nextExecutionTime = formatter.format(nextExecutionTime),
                        tags = tags,
                        projectName = context.projectName ?: "unknown",
                        taskHistoryList = context.history.take(3).map {
                            TaskHistory(
                                success = it.success,
                                errorMessage = it.errorMessage,
                                errorType = it.errorType?.name ?: "без ошибок",
                                executionTime = formatter.format(it.executionTime)
                            )
                        }
                    )
                )

            }
        }


        val page = PageImpl(
            tasks, PageRequest.of(result.numberOfPages, resultSize), resultSize.toLong()
        )
        for (pageItem in page) {
            println(pageItem)
        }
        model.addAttribute("pageNumbers", 1..result.numberOfPages)

        model.addAttribute("taskPage", page)

        model.addAttribute("tags", tagRepository.findAll().map { it.name })
        return "index.html"
    }

    data class Task(
        val status: String, val name: String, val id: String, val nextExecutionTime: String,

        val projectName: String,
        val tags: List<String> = listOf(),
        val taskHistoryList: List<TaskHistory> = listOf()
    )


    data class TaskHistory(
        var success: Boolean,
        var errorMessage: String?,
        var errorType: String,
        var executionTime: String
    )
}