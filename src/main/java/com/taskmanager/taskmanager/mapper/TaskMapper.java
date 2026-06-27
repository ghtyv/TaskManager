package com.taskmanager.taskmanager.mapper;

import com.taskmanager.taskmanager.dto.TaskCreateSpecificationDto;
import com.taskmanager.taskmanager.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskCreateSpecificationDto toDto(Task task) {
        String title = task.getTitle();
        String description = task.getDescription();
        Long assigneeId = null;

        if (task.getAssignee() != null) {
            assigneeId = task.getAssignee().getId();
        }

        return  new TaskCreateSpecificationDto(title, description, assigneeId);
    }

}
