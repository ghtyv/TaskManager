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
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskResponseDto> findAllByParentTaskIsNullAndDeletedIsFalse() {
        return taskService.findAllByParentTaskIsNullAndDeletedIsFalse();
    }

    @GetMapping("/{id}")
    public TaskResponseDto findTaskById(@PathVariable("id") Long id) {
        return taskService.findTaskByIdAndDeletedIsFalse(id);
    }

    @GetMapping("/open")
    @ResponseBody
    public List<TaskResponseDto> findAllByOpenAndParentTaskIsNullAndDeletedIsFalse(@RequestParam boolean isOpen) {
        return taskService.findAllByOpenAndParentTaskIsNullAndDeletedIsFalse(isOpen);
    }

    @PatchMapping("/{id}")
    public TaskResponseDto updateTask(
            @PathVariable("id") Long id,
            @Valid @RequestBody ParentTaskUpdateSpecificationDto parentTaskUpdateSpecificationDto) {
        return taskService.updateTask(id, parentTaskUpdateSpecificationDto);
    }

    @PostMapping
    public TaskResponseDto createTask(@Valid @RequestBody TaskCreateSpecificationDto taskCreateSpecificationDto) {
        return taskService.createTask(taskCreateSpecificationDto);
    }

    @DeleteMapping("/{parentTaskId}/subtasks/{subTaskId}")
    public void removeSubTask(@PathVariable("parentTaskId") Long parentTaskId,
                              @PathVariable("subTaskId") Long subTaskId) {
        taskService.removeSubTask(parentTaskId, subTaskId);
    }

}
