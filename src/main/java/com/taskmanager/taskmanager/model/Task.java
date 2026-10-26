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

    @OneToMany(mappedBy = "parentTask")
    private List<Task> subTasks = new ArrayList<>();

    @Column (name = "deleted", nullable = false, columnDefinition = "BOOLEAN")
    private boolean deleted = false;

    public Task(String title, String description, LocalDateTime deadline, User assignee, Task parentTask) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.assignee = assignee;
        this.parentTask = parentTask;
    }

    public void addSubTask(Task subTask) {
        subTasks.add(subTask);
        subTask.setParentTask(this);
    }

    public void removeSubTask(Task subTask) {
        subTasks.remove(subTask);
        subTask.setParentTask(null);
    }

}
