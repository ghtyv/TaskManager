package com.taskmanager.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TaskUpdateSpecificationDto {

    private String title;

    private String description;

    private Boolean open;

    private LocalDateTime deadline;

    private Long assigneeId;

    @JsonIgnore
    private boolean titleProvided;

    @JsonIgnore
    private boolean descriptionProvided;

    @JsonIgnore
    private boolean openProvided;

    @JsonIgnore
    private boolean deadlineProvided;

    @JsonIgnore
    private boolean assigneeIdProvided;

    @JsonSetter("title")
    public void setTitle(String title) {
        this.title = title;
        this.titleProvided = true;
    }

    @JsonSetter("description")
    public void setDescription(String description) {
        this.description = description;
        this.descriptionProvided = true;
    }

    @JsonSetter("open")
    public void setOpen(Boolean open) {
        this.open = open;
        this.openProvided = true;
    }

    @JsonSetter("deadline")
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
        this.deadlineProvided = true;
    }

    @JsonSetter("assigneeId")
    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
        this.assigneeIdProvided = true;
    }

    @AssertTrue(message = "Title must not be blank when provided")
    @JsonIgnore
    public boolean isTitleValid() {
        return !titleProvided || (title != null && !title.isBlank());
    }

    @AssertTrue(message = "Open must not be null when provided")
    @JsonIgnore
    public boolean isOpenValid() {
        return !openProvided || open != null;
    }
}
