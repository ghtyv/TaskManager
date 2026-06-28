package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.TaskDto;
import com.taskmanager.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.taskmanager.mapper.TaskMapper;
import com.taskmanager.taskmanager.model.Task;
import com.taskmanager.taskmanager.model.User;
import com.taskmanager.taskmanager.repository.TaskRepository;
import com.taskmanager.taskmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public List<TaskDto> findAllTasks() {
        return taskMapper.listToDto(taskRepository.findAll());
    }

    @Transactional
    public TaskDto findTaskById(Long id) {
        var ex = new ResourceNotFoundException();

        var task = taskRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task With Id " + id + " Not Found",
                                ex)
                );
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskDto updateTask(Long id, TaskDto taskDto) {
        var ex = new ResourceNotFoundException();

        var task = taskRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task With Id " + id + " Not Found",
                                ex)
                );

        if (taskDto.getTitle() != null) {
            task.setTitle(taskDto.getTitle());
        }

        if (taskDto.getDescription() != null) {
            task.setDescription(taskDto.getDescription());
        }

        if (taskDto.getOpen() != null) {
            task.setOpen(taskDto.getOpen());
        }

        if (taskDto.getAssigneeId() != null) {
            var assignee = userRepository.findById(taskDto.getAssigneeId())
                    .orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Provided Assignee Not Found",
                                    ex)
                    );
            task.setAssignee(assignee);
        }

        var savedTask = taskRepository.save(task);

        return taskMapper.toDto(savedTask);
    }

    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        var ex = new ResourceNotFoundException();

        taskDto.setOpen(true);
        String title = null;
        User assignee = null;

        if (taskDto.getTitle() != null) {
            title = taskDto.getTitle()
                    .describeConstable().orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,
                                    "Provided Title " + taskDto.getTitle() + " Is Not Valid")
                    );
        }

        if (taskDto.getAssigneeId() != null) {
            assignee = userRepository.findById(taskDto.getAssigneeId())
                    .orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Provided Assignee Not Found",
                                    ex)
                    );
        }

        var task = new Task(title, taskDto.getDescription(), assignee);
        var savedTask = taskRepository.save(task);

        return taskMapper.toDto(savedTask);
    }

    @Transactional
    public List<TaskDto> findAllByOpen(boolean isOpen) {
        return taskMapper.listToDto(taskRepository.findAllByOpen(isOpen));
    }
}
