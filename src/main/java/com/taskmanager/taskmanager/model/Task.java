package com.taskmanager.taskmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
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

    @ManyToOne
    @JoinColumn(name = "assignee_user_id", referencedColumnName = "id")
    private User assignee;

    public Task(String title, String description, User assignee) {
        this.title = title;
        this.description = description;
        this.assignee = assignee;
    }
}
