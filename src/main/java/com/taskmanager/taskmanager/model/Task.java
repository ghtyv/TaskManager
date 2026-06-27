package com.taskmanager.taskmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table (name = "tasks")
public class Task {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column (name = "title", nullable = false, length = 255)
    private String title;

    @Getter
    @Setter
    @Column (name = "description", columnDefinition = "TEXT")
    private String description;

    @Getter
    @Setter
    @Column (name = "open", nullable = false)
    private Boolean open = true;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "assignee_user_id", referencedColumnName = "id")
    private User assignee;

    public Task(String title, String description, User assignee) {
        this.title = title;
        this.description = description;
        this.assignee = assignee;
    }
}
