package com.taskmanager.taskmanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {

    private String title;
    private String description;
    private boolean open = true;
    private Long assigneeId;

    public TaskDto(String title, String description, Long assigneeId) {
        this.title = title;
        this.description = description;
        this.assigneeId = assigneeId;
    }
}
