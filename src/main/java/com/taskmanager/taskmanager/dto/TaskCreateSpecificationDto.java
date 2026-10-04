package com.taskmanager.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateSpecificationDto {

    @NotBlank
    private String title;

    private String description;
    private LocalDateTime deadline;
    private Long assigneeId;

}
