package com.taskmanager.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProjectUpdateSpecificationDto {

    private String title;

    private String description;

    private Boolean open;

    private Long leadId;

    @JsonIgnore
    private boolean titleProvided;

    @JsonIgnore
    private boolean descriptionProvided;

    @JsonIgnore
    private boolean openProvided;

    @JsonIgnore
    private boolean leadIdProvided;

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

    @JsonSetter("leadId")
    public void setLeadId(Long assigneeId) {
        this.leadId = assigneeId;
        this.leadIdProvided = true;
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
