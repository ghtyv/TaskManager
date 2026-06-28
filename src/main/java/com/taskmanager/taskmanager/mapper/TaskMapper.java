package com.taskmanager.taskmanager.mapper;

import com.taskmanager.taskmanager.dto.TaskDto;
import com.taskmanager.taskmanager.model.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskMapper {

    public TaskDto toDto(Task task) {
        String title = task.getTitle();
        String description = task.getDescription();
        Boolean open = task.getOpen();
        Long assigneeId = null;

        if (task.getAssignee() != null) {
            assigneeId = task.getAssignee().getId();
        }

        return  new TaskDto(title, description, open, assigneeId);
    }

    public List<TaskDto> listToDto(List<Task> taskList) {
        return taskList
                .stream()
                .map(this::toDto)
                .toList();
    }

}
