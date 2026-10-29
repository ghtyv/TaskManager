package com.taskmanager.taskmanager.controller;

import com.taskmanager.taskmanager.dto.TaskCreateSpecificationDto;
import com.taskmanager.taskmanager.dto.TaskResponseDto;
import com.taskmanager.taskmanager.dto.ParentTaskUpdateSpecificationDto;
import com.taskmanager.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskResponseDto> findAllByParentTaskIsNullAndDeletedIsFalse(@PathVariable("projectId") Long projectId) {
        return taskService.findAllByParentTaskIsNullAndDeletedIsFalse(projectId);
    }

    @GetMapping("/open")
    @ResponseBody
    public List<TaskResponseDto> findAllByOpenAndParentTaskIsNullAndDeletedIsFalse(
            @PathVariable("projectId") Long projectId,
            @RequestParam boolean isOpen) {
        return taskService.findAllByOpenAndParentTaskIsNullAndDeletedIsFalse(projectId, isOpen);
    }

    @GetMapping("/{taskId}")
    public TaskResponseDto findTaskByIdAndDeletedIsFalse(@PathVariable("projectId") Long projectId,
                                                         @PathVariable("taskId") Long taskId) {
        return taskService.findTaskByIdAndDeletedIsFalse(projectId, taskId);
    }

    @PatchMapping("/{taskId}")
    public TaskResponseDto updateTask(
            @PathVariable("projectId") Long projectId,
            @PathVariable("taskId") Long parentTaskId,
            @Valid @RequestBody ParentTaskUpdateSpecificationDto parentTaskUpdateSpecificationDto) {
        return taskService.updateTask(projectId, parentTaskId, parentTaskUpdateSpecificationDto);
    }

    @PostMapping
    public TaskResponseDto createTask(
            @PathVariable("projectId") Long projectId,
            @Valid @RequestBody TaskCreateSpecificationDto taskCreateSpecificationDto) {
        return taskService.createTask(projectId, taskCreateSpecificationDto);
    }

    @DeleteMapping("/{taskId}")
    public void removeTask(@PathVariable("projectId") Long projectId,
                           @PathVariable("taskId") Long taskId) {
        taskService.removeTask(projectId, taskId);
    }

    @DeleteMapping("/{parentTaskId}/subtasks/{subTaskId}")
    public void removeSubTask(@PathVariable("projectId") Long projectId,
                              @PathVariable("parentTaskId") Long parentTaskId,
                              @PathVariable("subTaskId") Long subTaskId) {
        taskService.removeSubTask(projectId, parentTaskId, subTaskId);
    }

}
