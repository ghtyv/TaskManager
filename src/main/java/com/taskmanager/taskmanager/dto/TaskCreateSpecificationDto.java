package com.taskmanager.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskCreateSpecificationDto {

    private String title;
    private String description;
    private Long assigneeId;

}
