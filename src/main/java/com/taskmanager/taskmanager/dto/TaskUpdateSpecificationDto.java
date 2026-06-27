package com.taskmanager.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskUpdateSpecificationDto {

    private String title;
    private String description;
    private boolean open;
    private Long assigneeId;

}
