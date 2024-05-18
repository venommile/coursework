package ru.galtsev.coursework.e2e_framework.ui.controller

import org.springframework.web.bind.annotation.*
import ru.galtsev.coursework.e2e_framework.ui.dto.GetTasksResponse
import ru.galtsev.coursework.e2e_framework.ui.dto.PollResponse
import ru.galtsev.coursework.e2e_framework.ui.dto.TaskDetailsRequestParams
import ru.galtsev.coursework.e2e_framework.ui.dto.TaskRequestParams
import ru.galtsev.coursework.e2e_framework.ui.service.TaskLogic
import java.time.Instant


@RestController
@CrossOrigin
@RequestMapping("/tasks")
class TaskController(private val taskLogic: TaskLogic) {
    @GetMapping("/all")
    fun getTasks(params: TaskRequestParams): GetTasksResponse {
        return taskLogic.getAllTasks(params)
    }

    @GetMapping("/details")
    fun getTaskDetails(params: TaskDetailsRequestParams): GetTasksResponse {
        return taskLogic.getTask(params)
    }

    @GetMapping("/poll")
    fun pollForUpdates(params: TaskDetailsRequestParams): PollResponse {
        return taskLogic.pollTasks(params)
    }

    @PostMapping("/rerun")
    fun runNow(
        @RequestParam id: String, @RequestParam name: String, @RequestParam scheduleTime: Instant?
    ) {

        println("тута")
        println("$name, $scheduleTime, $id")
        var resultTime = scheduleTime
        if (scheduleTime == null || scheduleTime.isBefore(Instant.now())) {
            resultTime = Instant.now()
        }
        taskLogic.runTaskNow(id, name, resultTime)
    }

    @PostMapping("/rerunGroup")
    fun runAllNow(@RequestParam name: String, @RequestParam onlyFailed: Boolean) {
        taskLogic.runTaskGroupNow(name, onlyFailed)
    }

    @PostMapping("/delete")
    fun deleteTaskNow(@RequestParam id: String, @RequestParam name: String) {
        taskLogic.deleteTask(id, name)
    }
}