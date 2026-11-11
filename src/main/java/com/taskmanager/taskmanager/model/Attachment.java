package com.taskmanager.taskmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "objectKey", nullable = false)
    private String objectKey;

    @Column (name = "name", nullable = false, columnDefinition = "TEXT", length = 255)
    private String name;

    @Column (name = "size", nullable = false)
    private Long size;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;

    public Attachment(String objectKey, String name, Long size, Project project, Task task) {
        this.objectKey = objectKey;
        this.name = name;
        this.size = size;
        this.task = task;
        this.project = project;
    }
}
