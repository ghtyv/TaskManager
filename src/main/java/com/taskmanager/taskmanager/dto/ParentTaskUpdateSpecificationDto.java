package com.taskmanager.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ParentTaskUpdateSpecificationDto {

    private String title;

    private String description;

    private Boolean open;

    private LocalDateTime deadline;

    private Long assigneeId;

    private List<@NotNull @Valid SubTaskUpdateSpecificationDto> subTasks;

    private List<@NotNull Long> removedSubtaskIds;

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

    @JsonIgnore
    private boolean subTasksProvided;

    @JsonIgnore
    private boolean removedSubtaskIdsProvided;

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

    @JsonSetter("subTasks")
    public void setSubTasks(List<SubTaskUpdateSpecificationDto> subTasks) {
        this.subTasks = subTasks;
        this.subTasksProvided = true;
    }

    @JsonSetter("removedSubtaskIds")
    public void setRemovedSubtaskIds(List<Long> removedSubtaskIds) {
        this.removedSubtaskIds = removedSubtaskIds;
        this.removedSubtaskIdsProvided = true;
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

    @AssertTrue(message = "SubTasks must not be null when provided")
    @JsonIgnore
    public boolean isSubTasksValid() {
        return !subTasksProvided || subTasks != null;
    }

    @AssertTrue(message = "RemovedSubtaskIds must not be null when provided")
    @JsonIgnore
    public boolean isRemovedSubtaskIdsValid() {
        return !removedSubtaskIdsProvided || removedSubtaskIds != null;
    }
}
