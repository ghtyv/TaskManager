package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.TaskCreateSpecificationDto;
import com.taskmanager.taskmanager.dto.TaskResponseDto;
import com.taskmanager.taskmanager.dto.TaskUpdateSpecificationDto;
import com.taskmanager.taskmanager.mapper.TaskMapper;
import com.taskmanager.taskmanager.model.Task;
import com.taskmanager.taskmanager.model.User;
import com.taskmanager.taskmanager.repository.TaskRepository;
import com.taskmanager.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// - Для updateTask добавить каким-то образом возможность снять исполнителя.

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional(readOnly = true)
    public List<TaskResponseDto> findAllTasks() {
        return taskMapper.listToDto(taskRepository.findAll());
    }

    @Transactional(readOnly = true)
    public TaskResponseDto findTaskById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task With Id " + id + " Not Found")
                );
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskResponseDto updateTask(Long id, TaskUpdateSpecificationDto taskUpdateSpecificationDto) {
        var task = taskRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task With Id " + id + " Not Found")
                );

        task.setTitle(taskUpdateSpecificationDto.getTitle());
        task.setDescription(taskUpdateSpecificationDto.getDescription());
        task.setOpen(taskUpdateSpecificationDto.getOpen());

        if (taskUpdateSpecificationDto.getAssigneeId() != null) {
            var assignee = userRepository.findById(taskUpdateSpecificationDto.getAssigneeId())
                    .orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Provided Assignee Not Found")
                    );
            task.setAssignee(assignee);
        }

        var savedTask = taskRepository.save(task);

        return taskMapper.toDto(savedTask);
    }

    @Transactional
    public TaskResponseDto createTask(TaskCreateSpecificationDto taskCreateSpecificationDto) {
        var title = taskCreateSpecificationDto.getTitle();
        var description = taskCreateSpecificationDto.getDescription();
        User assignee = null;

        if (taskCreateSpecificationDto.getAssigneeId() != null) {
            assignee = userRepository.findById(taskCreateSpecificationDto.getAssigneeId())
                    .orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Provided Assignee Not Found")
                    );
        }

        var task = new Task(title, description, assignee);
        var savedTask = taskRepository.save(task);

        return taskMapper.toDto(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> findAllByOpen(boolean isOpen) {
        return taskMapper.listToDto(taskRepository.findAllByOpen(isOpen));
    }
}
