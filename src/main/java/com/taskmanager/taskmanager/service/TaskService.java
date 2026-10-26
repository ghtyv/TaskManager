package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.SubTaskUpdateSpecificationDto;
import com.taskmanager.taskmanager.dto.TaskCreateSpecificationDto;
import com.taskmanager.taskmanager.dto.TaskResponseDto;
import com.taskmanager.taskmanager.dto.ParentTaskUpdateSpecificationDto;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional(readOnly = true)
    public List<TaskResponseDto> findAllByParentTaskIsNull() {
        return taskMapper.listToDto(taskRepository.findAllByParentTaskIsNull());
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

    private void updateParentTaskDetails(Task parentTask, ParentTaskUpdateSpecificationDto parentTaskUpdateSpecificationDto) {
        if (parentTaskUpdateSpecificationDto.isTitleProvided()) {
            parentTask.setTitle(parentTaskUpdateSpecificationDto.getTitle());
        }

        if (parentTaskUpdateSpecificationDto.isDescriptionProvided()) {
            parentTask.setDescription(parentTaskUpdateSpecificationDto.getDescription());
        }

        if (parentTaskUpdateSpecificationDto.isOpenProvided()) {
            parentTask.setOpen(parentTaskUpdateSpecificationDto.getOpen());
        }

        if (parentTaskUpdateSpecificationDto.isDeadlineProvided()) {
            parentTask.setDeadline(parentTaskUpdateSpecificationDto.getDeadline());
        }

        if (parentTaskUpdateSpecificationDto.isAssigneeIdProvided()) {
            var assigneeId = parentTaskUpdateSpecificationDto.getAssigneeId();

            if (assigneeId == null) {
                parentTask.setAssignee(null);
            } else {
                var assignee = userRepository.findById(assigneeId)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Provided Assignee Not Found")
                        );
                parentTask.setAssignee(assignee);
            }
        }

        taskRepository.save(parentTask);
    }

    private void createSubTask(SubTaskUpdateSpecificationDto subTaskUpdateSpecificationDto, Task parentTask) {
        var title = subTaskUpdateSpecificationDto.getTitle();
        var description = subTaskUpdateSpecificationDto.getDescription();
        LocalDateTime deadline = null;
        User assignee = null;

        if (subTaskUpdateSpecificationDto.getDeadline() != null) {
            deadline = subTaskUpdateSpecificationDto.getDeadline();
        }

        if (subTaskUpdateSpecificationDto.getAssigneeId() != null) {
            assignee = userRepository.findById(subTaskUpdateSpecificationDto.getAssigneeId())
                    .orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Provided Assignee Not Found")
                    );
        }

        var subTask = new Task(title, description, deadline, assignee, parentTask);
        parentTask.addSubTask(subTask);

        taskRepository.save(subTask);
    }

    private void updateSubTask(SubTaskUpdateSpecificationDto subTaskUpdateSpecificationDto, Task parentTask) {
        var subTask = taskRepository.findById(subTaskUpdateSpecificationDto.getId())
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Provided SubTask Not Found")
                );

        /* Проверка на соответствие принадлежности подзадачи к родительской задаче: */

        if (subTask.getParentTask() == null || !Objects.equals(subTask.getParentTask().getId(), parentTask.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provided SubTask With Id " + subTask.getId() +
                            " Is Not A SubTask Of This Parent Task With Id " + parentTask.getId()
            );
        }

        if (subTaskUpdateSpecificationDto.isTitleProvided()) {
            subTask.setTitle(subTaskUpdateSpecificationDto.getTitle());
        }

        if (subTaskUpdateSpecificationDto.isDescriptionProvided()) {
            subTask.setDescription(subTaskUpdateSpecificationDto.getDescription());
        }

        if (subTaskUpdateSpecificationDto.isOpenProvided()) {
            subTask.setOpen(subTaskUpdateSpecificationDto.getOpen());
        }

        if (subTaskUpdateSpecificationDto.isDeadlineProvided()) {
            subTask.setDeadline(subTaskUpdateSpecificationDto.getDeadline());
        }

        if (subTaskUpdateSpecificationDto.isAssigneeIdProvided()) {
            var assigneeId = subTaskUpdateSpecificationDto.getAssigneeId();

            if (assigneeId == null) {
                subTask.setAssignee(null);
            } else {
                var assignee = userRepository.findById(assigneeId)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Provided Assignee Not Found")
                        );
                subTask.setAssignee(assignee);
            }
        }

        taskRepository.save(subTask);
    }

    @Transactional
    public TaskResponseDto updateTask(Long id, ParentTaskUpdateSpecificationDto parentTaskUpdateSpecificationDto) {
        var parentTask = taskRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task With Id " + id + " Not Found")
                );

        updateParentTaskDetails(parentTask, parentTaskUpdateSpecificationDto);

        if (parentTaskUpdateSpecificationDto.isSubTasksProvided()) {
            for (SubTaskUpdateSpecificationDto subTask : parentTaskUpdateSpecificationDto.getSubTasks()) {
                if (subTask.isIdProvided()) {
                    updateSubTask(subTask, parentTask);
                } else {
                    createSubTask(subTask, parentTask);
                }
            }
        }

        /* Логика удаления подзадач через страницу редактирования задачи. При подтверждении редактирования задачи
         пользователь отправляет список ID подзадач типа Long (removedSubTaskIds) для удаления (soft-delete): */

        if (parentTaskUpdateSpecificationDto.isRemovedSubtaskIdsProvided()) {
            for (Long removedSubTaskId : parentTaskUpdateSpecificationDto.getRemovedSubtaskIds()) {
                var subTask = taskRepository.findById(removedSubTaskId)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Provided SubTask With Id " + removedSubTaskId + " Not Found"
                                )
                        );

                /* Проверка на соответствие принадлежности подзадачи к родительской задаче: */

                if (subTask.getParentTask() == null || !Objects.equals(subTask.getParentTask().getId(), id)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Provided SubTask With Id " + removedSubTaskId +
                                    " Is Not A SubTask Of This Parent Task With Id " + id
                    );
                }

                makeSubTaskRemoved(subTask, parentTask);
            }
        }

        return taskMapper.toDto(parentTask);
    }

    private Task makeTask(TaskCreateSpecificationDto taskCreateSpecificationDto, Task parentTask) {
        var title = taskCreateSpecificationDto.getTitle();
        var description = taskCreateSpecificationDto.getDescription();
        LocalDateTime deadline = null;
        User assignee = null;

        if (taskCreateSpecificationDto.getDeadline() != null) {
            deadline = taskCreateSpecificationDto.getDeadline();
        }

        if (taskCreateSpecificationDto.getAssigneeId() != null) {
            assignee = userRepository.findById(taskCreateSpecificationDto.getAssigneeId())
                    .orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Provided Assignee Not Found")
                    );
        }

        var task = new Task(title, description, deadline, assignee, parentTask);
        if (parentTask != null) {
            parentTask.addSubTask(task);
        }

        return taskRepository.save(task);
    }

    @Transactional
    public TaskResponseDto createTask(TaskCreateSpecificationDto taskCreateSpecificationDto) {
        var savedParentTask = makeTask(taskCreateSpecificationDto, null);

        if (taskCreateSpecificationDto.getSubTasks() != null) {
            for (TaskCreateSpecificationDto subTask : taskCreateSpecificationDto.getSubTasks()) {
                makeTask(subTask, savedParentTask);
            }
        }

        return taskMapper.toDto(savedParentTask);
    }

    private void makeSubTaskRemoved(Task subTask, Task parentTask) {
        parentTask.removeSubTask(subTask);
        subTask.setDeleted(true);
    }

    @Transactional
    public void removeSubTask(Long parentTaskId, Long subTaskId) {
        var parentTask = taskRepository.findById(parentTaskId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Provided Parent Task With Id " + parentTaskId + " Not Found"
                        )
                );

        var subTask = taskRepository.findById(subTaskId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Provided SubTask With Id " + subTaskId + " Not Found"
                        )
                );

        /* Проверка на соответствие принадлежности подзадачи к родительской задаче: */

        if (subTask.getParentTask() == null || !Objects.equals(subTask.getParentTask().getId(), parentTask.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provided SubTask With Id " + subTaskId +
                            " Is Not A SubTask Of This Parent Task With Id " + parentTaskId
            );
        }

        makeSubTaskRemoved(subTask, parentTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> findAllByOpenAndParentTaskIsNull(boolean isOpen) {
        return taskMapper.listToDto(taskRepository.findAllByOpenAndParentTaskIsNull(isOpen));
    }
}
