package com.taskmanager.taskmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table (name = "tasks")
public class Task {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "title", nullable = false, length = 255)
    private String title;

    @Column (name = "description", columnDefinition = "TEXT")
    private String description;

    @Column (name = "open", nullable = false)
    private boolean open = true;

    @Column (name = "deadline")
    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "assignee_user_id", referencedColumnName = "id")
    private User assignee;

    @ManyToOne
    @JoinColumn(name = "parent_task_id", referencedColumnName = "id")
    private Task parentTask;

    /* Список subTasks нужен для того, чтобы у объекта родительской задачи в Java был доступ к его подзадачам.
    * В БД отдельного столбца "subTasks" не создается. "@OneToMany(mappedBy = "parentTask")" означает, что у одной Task
    * может быть много других "Task", у которых поле "parentTask" указывает на эту задачу. mappedBy = "parentTask"
    * означает, что не нужно создавать новую колонку для "subTasks", используется уже существующее поле "parentTask"
    * в сущности "Task". "new ArrayList<>()" нужен, чтобы список изначально был не null (а был "subTasks = []",
    * то есть пустым списком), без этого новая задача до загрузки из БД могла бы иметь "subTasks == null", и тогда
    * "subTasks.add(subTask);" даст "NullPointerException".
    * Почему одной строки subTask.setParentTask(parent) недостаточно для поддержки модели задачи в памяти в
    * согласованном состоянии? Для БД этого часто достаточно, потому что parentTask является владельцем связи. То есть:
    * subTask.setParentTask(parent);
    * taskRepository.save(subTask);
    * может корректно записать в БД:
    * parent_task_id = parent.id
    * Но объект parent в памяти всё ещё может иметь:
    * parent.getSubTasks() == []
    * То есть БД уже знает о ребёнке, а Java-объект родителя — ещё нет.
    * Потом ты делаешь:
    * taskMapper.toDto(parent)
    * mapper смотрит:
    * parent.getSubTasks()
    * и не видит только что созданную подзадачу. Поэтому addSubTask() нужен именно для поддержания модели в памяти в
    * согласованном состоянии. */

    @OneToMany(mappedBy = "parentTask")
    private List<Task> subTasks = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    @Column (name = "deleted", nullable = false, columnDefinition = "BOOLEAN")
    private boolean deleted = false;

    public Task(String title, String description, LocalDateTime deadline, User assignee, Task parentTask, Project project) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.assignee = assignee;
        this.parentTask = parentTask;
        this.project = project;
    }

    public void addSubTask(Task subTask) {
        subTasks.add(subTask);
        subTask.setParentTask(this);
    }

    public void removeSubTask(Task subTask) {
        subTasks.remove(subTask);
    }

}
