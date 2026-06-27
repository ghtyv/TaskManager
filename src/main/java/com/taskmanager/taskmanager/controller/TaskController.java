package com.taskmanager.taskmanager.controller;

import com.taskmanager.taskmanager.dto.TaskDto;
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
    public List<TaskDto> findAllTasks() {
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

    @PutMapping("/{id}/close")
    public Task closeTaskById(@PathVariable("id") Long id) {
        return taskService.changeTaskById(id);
    }

    @PutMapping("{id}/open")
    public Task openTaskById(@PathVariable("id") Long id) {
        return taskService.openTaskById(id);
    }

    @PostMapping
    public TaskDto createTask(@RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

}
