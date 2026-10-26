package com.taskmanager.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime deadline;
    private Long assigneeId;
    private Long parentTaskId;
    private List<TaskResponseDto> subTasks;
}
