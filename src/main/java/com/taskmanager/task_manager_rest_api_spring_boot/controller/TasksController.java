package com.taskmanager.task_manager_rest_api_spring_boot.controller;

import com.taskmanager.task_manager_rest_api_spring_boot.model.Tasks;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.taskmanager.task_manager_rest_api_spring_boot.repository.TasksRepository;
import com.taskmanager.task_manager_rest_api_spring_boot.service.TasksService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TasksController {

    @Autowired
    private TasksService tasksService;

    @Autowired
    private TasksRepository tasksRepository;

    @GetMapping("/tasks")
    public List<Tasks> findAllTasks() {

        return tasksService.findAllTasks();

    }

    @GetMapping("/task/{id}")
    public Tasks findTaskById(@PathVariable("id") Long id) {

        return tasksService.findTaskById(id).orElseThrow();

    }

    @GetMapping("/tasks/open")
    public List<Tasks> findByOpenTrue() {

        return tasksRepository.findByOpenTrue();

    }

    @GetMapping("/tasks/closed")
    public List<Tasks> findByOpenFalse() {

        return tasksRepository.findByOpenFalse();

    }

    @PutMapping("/task/{id}/close")
    public Tasks closeTaskById(@PathVariable("id") Long id) {

        return tasksService.closeTaskById(id);

    }

    @PutMapping("/task/{id}/open")
    public  Tasks openTaskById(@PathVariable("id") Long id) {

        return tasksService.openTaskById(id);

    }

    @PostMapping("/task/create")
    public Tasks createTask(@RequestBody @NonNull Tasks task) {

        String title = task.getTitle();
        String description = task.getDescription();

        return tasksService.createTask(title, description);

    }

}
