package com.taskmanager.task_manager_rest_api_spring_boot;


import jakarta.persistence.*;

@Entity
@Table (name = "tasks")
public class Tasks {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;

    @Column (name = "title", nullable = false)
    private String title;

    @Column (name = "description")
    private String description;

    @Column (name = "open", nullable = false)
    private Boolean open = true;

    @ManyToOne
    @JoinColumn(name = "assignee_user_id", referencedColumnName = "id")
    private Users assignee_user_id;

    /* first time */

}
