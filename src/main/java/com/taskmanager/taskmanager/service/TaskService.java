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

        User assignee = null;
        var task = findTaskById(id).orElseThrow();


        task.setDescription(taskDto.getDescription());
        task.setOpen(taskDto.isOpen()); /* может нужен if statement для проверки на isOpen != null?
                                           не знаю как на фронте будет передаваться статус задачи
                                           для его изменения, мб там по умолчанию автоматически
                                           будет выбираться действующий статус задачи, а может
                                           как раз никакой (null) статус по умолчанию не
                                           выбираться */

        if (taskDto.getAssigneeId() != null) {
            assignee = userRepository.findUserById(taskDto.getAssigneeId())
                    .orElseThrow();
        }

        task.setAssignee(assignee);

        taskRepository.save(task);

        return taskDto;
    }

    public TaskDto createTask(TaskDto taskDto) {
        User assignee = null;

        if (taskDto.getAssigneeId() != null) {
            assignee = userRepository.findUserById(taskDto.getAssigneeId())
                    .orElseThrow();
        }

        var task = new Task(taskDto.getTitle(), taskDto.getDescription(), assignee);
        taskRepository.save(task);

        return taskDto;
    }

    public List<Task> findAllByOpen(boolean isOpen) {
        return taskRepository.findAllByOpen(isOpen);
    }
}
