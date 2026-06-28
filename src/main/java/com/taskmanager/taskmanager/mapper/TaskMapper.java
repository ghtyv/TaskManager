package com.taskmanager.taskmanager.mapper;

import com.taskmanager.taskmanager.dto.TaskResponseDto;
import com.taskmanager.taskmanager.model.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskMapper {

    public TaskResponseDto toDto(Task task) {
        Long id = task.getId();
        String title = task.getTitle();
        String description = task.getDescription();
        boolean open = task.isOpen();
        Long assigneeId = null;

        if (task.getAssignee() != null) {
            assigneeId = task.getAssignee().getId();
        }

        return new TaskResponseDto(id, title, description, open, assigneeId);
    }

    public List<TaskResponseDto> listToDto(List<Task> taskList) {
        return taskList
                .stream()
                .map(this::toDto)
                .toList();
    }

}
