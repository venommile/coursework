package ru.galtsev.coursework.e2e_framework.ui.dto

class PollResponse(
    val newFailures: Int,
    val newRunning: Int,
    val newTasks: Int,
    val stoppedFailing: Int,
    val finishedRunning: Int
)