package com.taskmanager.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskUpdateSpecificationDto {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Boolean open;

    private Long assigneeId;

}
