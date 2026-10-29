package com.taskmanager.taskmanager.service;

import com.taskmanager.taskmanager.dto.ParentTaskUpdateSpecificationDto;
import com.taskmanager.taskmanager.dto.SubTaskUpdateSpecificationDto;
import com.taskmanager.taskmanager.dto.TaskCreateSpecificationDto;
import com.taskmanager.taskmanager.dto.TaskResponseDto;
import com.taskmanager.taskmanager.mapper.TaskMapper;
import com.taskmanager.taskmanager.model.Project;
import com.taskmanager.taskmanager.model.Task;
import com.taskmanager.taskmanager.model.User;
import com.taskmanager.taskmanager.repository.ProjectRepository;
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
    private final ProjectRepository projectRepository;

    /* Находит все родительские задачи данного проекта в БД через репозиторий: */

    @Transactional(readOnly = true)
    public List<TaskResponseDto> findAllByParentTaskIsNullAndDeletedIsFalse(Long projectId) {
        projectRepository.findByIdAndDeletedIsFalse(projectId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Provided Project Not Found"
                        )
                );

        return taskMapper.listToDto(taskRepository.findAllByProjectIdAndParentTaskIsNullAndDeletedIsFalse(projectId));
    }

    /* Находит все родительские задачи данного проекта в БД через репозиторий с учетом актуальности
    (состояния жизненного цикла) задачи isOpen: */

    @Transactional(readOnly = true)
    public List<TaskResponseDto> findAllByOpenAndParentTaskIsNullAndDeletedIsFalse(Long projectId ,boolean isOpen) {
        projectRepository.findByIdAndDeletedIsFalse(projectId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Provided Project Not Found"
                        )
                );

        return taskMapper.listToDto(taskRepository
                .findAllByProjectIdAndOpenAndParentTaskIsNullAndDeletedIsFalse(projectId, isOpen));
    }

    /* Находит задачу данного проекта в БД через репозиторий: */

    @Transactional(readOnly = true)
    public TaskResponseDto findTaskByIdAndDeletedIsFalse(Long projectId, Long taskId) {
        projectRepository.findByIdAndDeletedIsFalse(projectId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Provided Project Of The Provided Task Not Found"
                        )
                );

        var task = taskRepository.findByIdAndDeletedIsFalse(taskId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task With Id " + taskId + " Not Found")
                );

        /* Проверка на соответствие принадлежности переданного (ID) проекта к проекту задачи: */

        if (projectId == null || !Objects.equals(projectId, task.getProject().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provided Project Is Not A Project Of The Provided Task"
            );
        }

        /* Если сама подзадача deleted = false, а её родитель deleted = true,
        она всё равно должна считаться недоступной для пользователя. */

        if (task.getParentTask() != null && task.getParentTask().isDeleted()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Task With Id " + taskId + " Not Found"
            );
        }

        return taskMapper.toDto(task);
    }

    /* Метод updateTask отвечает за логику редактирования родительской задачи. Данный метод использует helper-методы
    * updateParentTaskDetails (для обновления данных родительской задачи), updateSubTask, createSubTask
    * (для обновления/создания подзадачи) и makeSubTaskRemoved (для удаления (soft-delete) подзадачи): */

    @Transactional
    public TaskResponseDto updateTask(Long projectId, Long parentTaskId, ParentTaskUpdateSpecificationDto parentTaskUpdateSpecificationDto) {
        projectRepository.findByIdAndDeletedIsFalse(projectId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Project With Id " + projectId + " Not Found"
                        )
                );

        var parentTask = taskRepository.findByIdAndDeletedIsFalse(parentTaskId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task With Id " + parentTaskId + " Not Found")
                );

        /* Проверка на отсутствие подзадач (То есть действительно ли задача родительская): */

        if (parentTask.getParentTask() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provided Task Is Not A Parent Task"
            );
        }

        /* Проверка на соответствие принадлежности переданного (ID) проекта к проекту родительской задачи: */

        if (projectId == null || !Objects.equals(projectId, parentTask.getProject().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provided Project Is Not A Project Of The Parent Task"
            );
        }

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
                var subTask = taskRepository.findByIdAndDeletedIsFalse(removedSubTaskId)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Provided SubTask With Id " + removedSubTaskId + " Not Found"
                                )
                        );

                /* Проверка на соответствие принадлежности подзадачи к родительской задаче: */

                if (subTask.getParentTask() == null || !Objects.equals(subTask.getParentTask().getId(), parentTaskId)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Provided SubTask With Id " + removedSubTaskId +
                                    " Is Not A SubTask Of This Parent Task With Id " + parentTaskId
                    );
                }

                /* Проверка на соответствие принадлежности проекта подзадачи к проекту подительской задачи: */

                if (!Objects.equals(subTask.getProject().getId(), parentTask.getProject().getId())) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Provided SubTask's Project Does Not Match Provided Task's Project"
                    );
                }

                makeSubTaskRemoved(subTask);
            }
        }

        return taskMapper.toDto(parentTask);
    }

    /* Метод updateParentTaskDetails является helper-методом для обновления данных родительской задачи: */

    private void updateParentTaskDetails(Task parentTask,
                                         ParentTaskUpdateSpecificationDto parentTaskUpdateSpecificationDto) {
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

    /* Метод updateSubTask является helper-методом для обновления данных подзадачи: */

    private void updateSubTask(SubTaskUpdateSpecificationDto subTaskUpdateSpecificationDto, Task parentTask) {
        var subTask = taskRepository.findByIdAndDeletedIsFalse(subTaskUpdateSpecificationDto.getId())
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

        if (!Objects.equals(subTask.getProject().getId(), parentTask.getProject().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provided SubTask's Project Does Not Match Provided Task's Project"
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

    /* Метод createSubTask является helper-методом для создания подзадачи (в уже существующей родительской задачи): */

    private void createSubTask(SubTaskUpdateSpecificationDto subTaskUpdateSpecificationDto, Task parentTask) {
        var title = subTaskUpdateSpecificationDto.getTitle();
        var description = subTaskUpdateSpecificationDto.getDescription();
        LocalDateTime deadline = null;
        User assignee = null;

        var project = parentTask.getProject();

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

        var subTask = new Task(title, description, deadline, assignee, parentTask, project);
        parentTask.addSubTask(subTask);

        taskRepository.save(subTask);
    }

    /* Метод createTask отвечает за логику создания задачи. Данный метод использует helper-метод
    * makeTask (для записи данных и создания задачи/подзадачи): */

    @Transactional
    public TaskResponseDto createTask(Long projectId, TaskCreateSpecificationDto taskCreateSpecificationDto) {
        var project = projectRepository.findByIdAndDeletedIsFalse(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project Not Found"
                ));

        var savedParentTask = makeTask(taskCreateSpecificationDto, null, project);

        if (taskCreateSpecificationDto.getSubTasks() != null) {
            for (TaskCreateSpecificationDto subTask : taskCreateSpecificationDto.getSubTasks()) {
                makeTask(subTask, savedParentTask, project);
            }
        }

        return taskMapper.toDto(savedParentTask);
    }

    /* Метод makeTask является helper-методом для записи данных и создания новой задачи/подзадачи: */

    private Task makeTask(TaskCreateSpecificationDto taskCreateSpecificationDto, Task parentTask, Project project) {
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

        var task = new Task(title, description, deadline, assignee, parentTask, project);
        if (parentTask != null) {
            parentTask.addSubTask(task);
        }

        return taskRepository.save(task);
    }

    /* Метод removeTask отвечает за логику удаления (soft-delete) задачи: */

    @Transactional
    public void removeTask(Long projectId, Long taskId) {
        projectRepository.findByIdAndDeletedIsFalse(projectId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Project With Id " + projectId + " Not Found")
                );

        var task = taskRepository.findByIdAndDeletedIsFalse(taskId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task With Id " + taskId + " Not Found"
                        )
                );

        if (task.getParentTask() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Task With Id " + taskId + " Is Not A Parent Task"
            );
        }

        if (!Objects.equals(task.getProject().getId(), projectId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provided Project Is Not A Project Of The Provided Task"
            );
        }

        task.setDeleted(true);
        taskRepository.save(task);
    }

    /* Метод removeSubTask отвечает за логику удаления (soft-delete) подзадачи. Данный метод использует helper-метод
    * makeSubTaskRemoved (для удаления (soft-delete) подзадачи): */

    @Transactional
    public void removeSubTask(Long projectId, Long parentTaskId, Long subTaskId) {
        var parentTask = taskRepository.findByIdAndDeletedIsFalse(parentTaskId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Provided Parent Task With Id " + parentTaskId + " Not Found"
                        )
                );

        /* Проверка на соответствие принадлежности переданного (ID) проекта к проекту родительской задачи */

        if (!Objects.equals(parentTask.getProject().getId(), projectId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provided Project Is Not A Project Of The Parent Task"
            );
        }

        projectRepository.findByIdAndDeletedIsFalse(projectId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Project With Id " + projectId + " Not Found"
                        )
                );

        var subTask = taskRepository.findByIdAndDeletedIsFalse(subTaskId)
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

        /* Проверка на соответствие принадлежности проекта подзадачи к проекту родительской задачи: */

        if (subTask.getProject() == null || !Objects.equals(subTask.getProject().getId(),
                parentTask.getProject().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provided SubTask's Project Is Not A Project Of The Parent Task"
            );
        }

        makeSubTaskRemoved(subTask);
        taskRepository.save(subTask);
    }

    /* Метод makeSubTaskRemoved является helper-методом для удаления (soft-delete) подзадачи: */

    private void makeSubTaskRemoved(Task subTask) {
        subTask.setDeleted(true);
    }

}
