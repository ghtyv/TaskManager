package com.taskmanager.taskmanager.controller;

import com.taskmanager.taskmanager.dto.TaskCreateSpecificationDto;
import com.taskmanager.taskmanager.dto.TaskUpdateSpecificationDto;
import com.taskmanager.taskmanager.model.Task;
import com.taskmanager.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskCreateSpecificationDto> findAllTasks() {
        return taskService.findAllTasks();
    }

    @GetMapping("/{id}")
    public Task findTaskById(@PathVariable("id") Long id) {
        return taskService.findTaskById(id).orElseThrow();
    }

    @GetMapping("/open")
    @ResponseBody
    public List<Task> findAllByOpen(@RequestParam boolean isOpen) {
        return taskService.findAllByOpen(isOpen);
    }

    @PutMapping("/{id}/update")
    public TaskUpdateSpecificationDto updateTask(@PathVariable("id") Long id, @RequestBody TaskUpdateSpecificationDto
            taskUpdateSpecificationDto) {
        return taskService.updateTask(id, taskUpdateSpecificationDto);
    }

    @PostMapping
    public TaskCreateSpecificationDto createTask(@RequestBody TaskCreateSpecificationDto taskCreateSpecificationDto) {
        return taskService.createTask(taskCreateSpecificationDto);
    }

}
