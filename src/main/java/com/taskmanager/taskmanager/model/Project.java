package com.taskmanager.taskmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "title", nullable = false, length = 255)
    private String title;

    @Column (name = "description", columnDefinition = "TEXT")
    private String description;

    @Column (name = "open", nullable = false)
    private boolean open = true;

    /* Руководитель проекта (project_lead_id), может отсутствовать: */

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_lead_id", referencedColumnName = "id")
    private User lead;

    /* Список tasks нужен для того, чтобы у объекта типа Project в Java был доступ к его задачам.
     * В БД отдельного столбца "tasks" не создается. "@OneToMany(mappedBy = "project")" означает, что у одного Project
     * может быть много объектов типа "Task", у которых поле "project" указывает на этот проект. mappedBy = "project"
     * означает, что владельцем связи является Task.project. "new ArrayList<>()" нужен, чтобы список изначально был не null (а был "tasks = []",
     * то есть пустым списком), без этого новый проект до загрузки из БД мог бы иметь "tasks == null", и тогда
     * "tasks.add(task);" даст "NullPointerException".
     * Почему одной строки task.setProject(project) недостаточно для поддержки модели задачи в памяти в
     * согласованном состоянии? Для БД этого часто достаточно, потому что Task.project является владельцем связи. То есть:
     * task.setProject(project);
     * taskRepository.save(task);
     * может корректно записать в БД:
     * project_id = project.id
     * Но объект project в памяти всё ещё может иметь:
     * project.getTasks() == []
     * То есть БД уже знает о ребёнке, а Java-объект родителя — ещё нет.
     * Потом ты делаешь:
     * projectMapper.toDto(project)
     * mapper смотрит:
     * project.getTasks()
     * и не видит только что созданную задачу. Поэтому addTask() нужен именно для поддержания модели в памяти в
     * согласованном состоянии. */

    @OneToMany(mappedBy = "project")
    List<Task> tasks = new ArrayList<>();

    @Column (name = "deleted", nullable = false, columnDefinition = "BOOLEAN")
    private boolean deleted = false;

    public Project(String title, String description, User lead) {
        this.title = title;
        this.description = description;
        this.lead = lead;
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setProject(this);
    }

}
