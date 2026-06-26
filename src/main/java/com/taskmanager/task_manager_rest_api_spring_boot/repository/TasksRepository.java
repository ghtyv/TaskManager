package com.taskmanager.task_manager_rest_api_spring_boot.repository;

import com.taskmanager.task_manager_rest_api_spring_boot.model.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {

    List<Tasks> findByOpenFalse();
    List<Tasks> findByOpenTrue();

}

