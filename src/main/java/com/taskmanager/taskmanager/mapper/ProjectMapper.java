package com.taskmanager.taskmanager.mapper;

import com.taskmanager.taskmanager.dto.ProjectResponseDto;
import com.taskmanager.taskmanager.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectMapper {

    public ProjectResponseDto toDto(Project project) {
        Long id = project.getId();
        String title = project.getTitle();
        String description = project.getDescription();
        boolean open = project.isOpen();
        Long leadId = null;

        if (project.getLead() != null) {
            leadId = project.getLead().getId();
        }

        return new ProjectResponseDto(id, title, description, open, leadId);
    }

    public List<ProjectResponseDto> listToDto(List<Project> projectList) {
        return projectList
                .stream()
                .filter(project -> !project.isDeleted())
                .map(this::toDto)
                .toList();
    }

}
