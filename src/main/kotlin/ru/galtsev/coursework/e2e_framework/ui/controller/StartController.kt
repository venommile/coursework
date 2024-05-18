package ru.galtsev.coursework.e2e_framework.ui.controller

import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import kotlin.math.ceil


@Controller
class StartController {


    @GetMapping("/")
    fun index(
        model: Model,

        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): String {

        val resultSize = size ?: 5
        val resultPageIndex = page ?: 0

        val tasks = listOf(
            Task("Scheduled", "Task 1", "ke1_116969091", "18 May, 24 06:31"),
            Task("Scheduled", "Task 2", "ke1_116969092", "19 May, 24 07:45"),
            Task("Scheduled", "Task 2", "ke1_116969092", "19 May, 24 07:45"),
            Task("Scheduled", "Task 2", "ke1_116969092", "19 May, 24 07:45"),
            Task("Scheduled", "Task 2", "ke1_116969092", "19 May, 24 07:45"),
            Task("Scheduled", "Task 2", "ke1_116969092", "19 May, 24 07:45"),
            Task("Scheduled", "Task 2", "ke1_116969092", "19 May, 24 07:45"),
            Task("Scheduled", "Task 2", "ke1_116969092", "19 May, 24 07:45"),
            Task("Scheduled", "Task 2", "ke1_116969092", "19 May, 24 07:45"),
        )


        val page = PageImpl(
            tasks.drop(resultPageIndex * resultSize)
                .take(resultSize), PageRequest.of(resultPageIndex, resultSize), resultSize.toLong()
        )
        for (pageItem in page) {
            println(pageItem)
        }
        model.addAttribute("pageNumbers", 1..ceil(tasks.size.toDouble() / resultSize).toInt())

        model.addAttribute("taskPage", page)
        return "index.html"
    }

    data class Task(val status: String, val name: String, val id: String, val nextExecutionTime: String)
}