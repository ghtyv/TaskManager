package com.taskmanager.taskmanager.controller;

import com.taskmanager.taskmanager.dto.TaskDto;
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
    public TaskDto findTaskById(@PathVariable("id") Long id) {
        return taskService.findTaskById(id);
    }

    @GetMapping("/open")
    @ResponseBody
    public List<TaskDto> findAllByOpen(@RequestParam boolean isOpen) {
        return taskService.findAllByOpen(isOpen);
    }

    @PutMapping("/{id}/update")
    public TaskDto updateTask(@PathVariable("id") Long id, @RequestBody TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    @PostMapping
    public TaskDto createTask(@RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

}
