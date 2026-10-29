package com.taskmanager.taskmanager.controller;

import com.taskmanager.taskmanager.dto.*;
import com.taskmanager.taskmanager.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<ProjectResponseDto> findAllByDeletedIsFalse() {
        return projectService.findAllByDeletedIsFalse();
    }

    @GetMapping("/{projectId}")
    public ProjectResponseDto findByIdAndDeletedIsFalse(@PathVariable("projectId") Long projectId) {
        return projectService.findByIdAndDeletedIsFalse(projectId);
    }

    @GetMapping("/open")
    @ResponseBody
    public List<ProjectResponseDto> findAllByOpenAndDeletedIsFalse(
            @RequestParam boolean isOpen) {
        return projectService.findAllByOpenAndDeletedIsFalse(isOpen);
    }

    @PostMapping
    public ProjectResponseDto createProject(
            @Valid @RequestBody ProjectCreateSpecificationDto projectCreateSpecificationDto) {
        return projectService.createProject(projectCreateSpecificationDto);
    }

    @PatchMapping("/{projectId}")
    public ProjectResponseDto updateProject(
            @PathVariable("projectId") Long projectId,
            @Valid @RequestBody ProjectUpdateSpecificationDto projectUpdateSpecificationDto) {
        return projectService.updateProject(projectId, projectUpdateSpecificationDto);
    }

    @DeleteMapping("/{projectId}")
    public void removeProject(@PathVariable("projectId") Long projectId) {
        projectService.removeProject(projectId);
    }

}
