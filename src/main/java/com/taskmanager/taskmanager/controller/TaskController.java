package com.taskmanager.taskmanager.controller;

import com.taskmanager.taskmanager.dto.TaskCreateSpecificationDto;
import com.taskmanager.taskmanager.dto.TaskResponseDto;
import com.taskmanager.taskmanager.dto.TaskUpdateSpecificationDto;
import com.taskmanager.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskResponseDto> findAllTasks() {
        return taskService.findAllTasks();
    }

    @GetMapping("/{id}")
    public TaskResponseDto findTaskById(@PathVariable("id") Long id) {
        return taskService.findTaskById(id);
    }

    @GetMapping("/open")
    @ResponseBody
    public List<TaskResponseDto> findAllByOpen(@RequestParam boolean isOpen) {
        return taskService.findAllByOpen(isOpen);
    }

    @PutMapping("/{id}/update")
    public TaskResponseDto updateTask(@PathVariable("id") Long id, @Valid @RequestBody TaskUpdateSpecificationDto taskUpdateSpecificationDto) {
        return taskService.updateTask(id, taskUpdateSpecificationDto);
    }

    @PostMapping
    public TaskResponseDto createTask(@Valid @RequestBody TaskCreateSpecificationDto taskCreateSpecificationDto) {
        return taskService.createTask(taskCreateSpecificationDto);
    }

}
