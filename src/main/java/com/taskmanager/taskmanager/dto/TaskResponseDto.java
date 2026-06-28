package com.taskmanager.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Добавлено поле id для будущего фронта.

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private Boolean open;
    private Long assigneeId;
}
