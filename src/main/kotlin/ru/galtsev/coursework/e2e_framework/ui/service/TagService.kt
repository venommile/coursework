package ru.galtsev.coursework.e2e_framework.ui.service

import org.springframework.stereotype.Service
import ru.galtsev.coursework.e2e_framework.repository.TagRepository


@Service
class TagService(private val tagRepository: TagRepository) {


    fun findAllTags() = tagRepository.findAll()
}