package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.TaskDto;
import com.taskmanager.taskmanager.mapper.TaskMapper;
import com.taskmanager.taskmanager.model.Task;
import com.taskmanager.taskmanager.model.User;
import com.taskmanager.taskmanager.repository.TaskRepository;
import com.taskmanager.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public List<TaskDto> findAllTasks() {
        return taskRepository.findAllTasks()
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    public Optional<Task> findTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public TaskDto updateTask(Long id, TaskDto taskDto) {
        var task = findTaskById(id).orElseThrow();

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
            var assignee = userRepository.findUserById(taskDto.getAssigneeId())
                    .orElseThrow();
            task.setAssignee(assignee);
        }

        var savedTask = taskRepository.save(task);

        return taskMapper.toDto(savedTask);
    }

    public TaskDto createTask(TaskDto taskDto) {
        taskDto.setOpen(true);
        User assignee = null;

        if (taskDto.getAssigneeId() != null) {
            assignee = userRepository.findUserById(taskDto.getAssigneeId())
                    .orElseThrow();
        }

        var task = new Task(taskDto.getTitle(), taskDto.getDescription(), assignee);
        var savedTask = taskRepository.save(task);

        return taskMapper.toDto(savedTask);
    }

    public List<Task> findAllByOpen(boolean isOpen) {
        return taskRepository.findAllByOpen(isOpen);
    }
}
