package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.ProjectCreateSpecificationDto;
import com.taskmanager.taskmanager.dto.ProjectResponseDto;
import com.taskmanager.taskmanager.dto.ProjectUpdateSpecificationDto;
import com.taskmanager.taskmanager.mapper.ProjectMapper;
import com.taskmanager.taskmanager.model.Project;
import com.taskmanager.taskmanager.model.User;
import com.taskmanager.taskmanager.repository.ProjectRepository;
import com.taskmanager.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    /* Находит все проекты в БД через репозиторий: */

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> findAllByDeletedIsFalse() {
        return projectMapper.listToDto(projectRepository.findAllByDeletedIsFalse());
    }

    /* Находит все проекты в БД через репозиторий с учетом актуальности (состояния жизненного цикла) проектов.
    * При передаче в данный метод isOpen = true возвращаются все открытые проекты,
    * при передаче в данный метод isOpen = false возвращаются все закрытые проекты: */

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> findAllByOpenAndDeletedIsFalse(boolean isOpen) {
        return projectMapper.listToDto(projectRepository.findAllByOpenAndDeletedIsFalse(isOpen));
    }

    /* Находит определенный проект в БД через репозиторий с помощью ID: */

    @Transactional(readOnly = true)
    public ProjectResponseDto findByIdAndDeletedIsFalse(Long id) {
        var project = projectRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Project With Id " + id + " Not Found")
                );
        return projectMapper.toDto(project);
    }

    /* Метод createProject отвечает за логику создания проекта. savedProject используется для передачи актуальной
    * информации объекта типа Project мапперу (projectMapper): */

    @Transactional
    public ProjectResponseDto createProject (ProjectCreateSpecificationDto projectCreateSpecificationDto) {
        var title = projectCreateSpecificationDto.getTitle();
        var description = projectCreateSpecificationDto.getDescription();
        User lead = null;

        if (projectCreateSpecificationDto.getLeadId() != null) {
            lead = userRepository.findById(projectCreateSpecificationDto.getLeadId())
                    .orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Provided Lead Not Found")
                    );
        }

        var project = new Project(title, description, lead);
        var savedProject = projectRepository.save(project);

        return projectMapper.toDto(savedProject);
    }

    /* Метод updateProject отвечает за логику редактирования проекта: */

    @Transactional
    public ProjectResponseDto updateProject(Long id, ProjectUpdateSpecificationDto projectUpdateSpecificationDto) {
        var project = projectRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Project With Id " + id + " Not Found")
                );

        if (projectUpdateSpecificationDto.isTitleProvided()) {
            project.setTitle(projectUpdateSpecificationDto.getTitle());
        }

        if (projectUpdateSpecificationDto.isDescriptionProvided()) {
            project.setDescription(projectUpdateSpecificationDto.getDescription());
        }

        if (projectUpdateSpecificationDto.isOpenProvided()) {
            project.setOpen(projectUpdateSpecificationDto.getOpen());
        }

        if (projectUpdateSpecificationDto.isLeadIdProvided()) {
            var leadId = projectUpdateSpecificationDto.getLeadId();

            if (leadId == null) {
                project.setLead(null);
            } else {
                var lead = userRepository.findById(leadId)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Provided Lead Not Found")
                        );
                project.setLead(lead);
            }
        }

        var savedProject = projectRepository.save(project);

        return projectMapper.toDto(savedProject);
    }

    /* Метод removeProject отвечает за логику удаления (soft-delete) проекта: */

    @Transactional
    public void removeProject(Long id) {
        var project = projectRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Project With Id " + id + " Not Found")
                );



        project.setDeleted(true);
    }

}
