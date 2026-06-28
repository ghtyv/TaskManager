package com.taskmanager.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateSpecificationDto {

    @NotBlank
    private String title;

    private String description;
    private Long assigneeId;

}
